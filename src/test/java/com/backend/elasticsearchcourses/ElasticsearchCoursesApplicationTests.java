package com.backend.elasticsearchcourses;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.backend.elasticsearchcourses.document.CourseDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ElasticsearchCoursesApplicationTests {

    @Container
    static ElasticsearchContainer elasticsearch =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.15.0")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false");

    static ElasticsearchClient client;

    @BeforeAll
    static void setup() throws Exception {
        client = ElasticsearchTestUtils.createClient(elasticsearch);

        client.indices().create(c -> c
                .index("courses")
                .mappings(m -> m
                        .properties("category", p -> p.keyword(k -> k))
                        .properties("type", p -> p.keyword(k -> k))                )
        );

        List<CourseDocument> subset = ElasticsearchTestUtils.sampleSubset();
        for (CourseDocument course : subset) {
            client.index(IndexRequest.of(i -> i
                    .index("courses")
                    .id(course.getId())
                    .document(course)
            ));
        }

        client.indices().refresh(r -> r.index("courses"));
    }

    @Test
    void fuzzySearchShouldMatchAlgebra() throws Exception {
        SearchResponse<CourseDocument> response = client.search(s -> s
                        .index("courses")
                        .query(q -> q.match(m -> m
                                .field("title")
                                .query("algibra")
                                .fuzziness("AUTO")
                        )),
                CourseDocument.class);

        assertThat(response.hits().hits())
                .extracting(Hit::source)
                .anySatisfy(course ->
                        assertThat(course.getTitle()).contains("Algebra")
                );
    }

    @Test
    void filterShouldReturnLanguageOnly() throws Exception {
        SearchResponse<CourseDocument> response = client.search(s -> s
                        .index("courses")
                        .query(q -> q.term(t -> t.field("category").value("Art"))),
                CourseDocument.class);

        assertThat(response.hits().hits())
                .extracting(Hit::source)
                .hasSize(2)
                .extracting(CourseDocument::getTitle)
                .containsExactlyInAnyOrder("Creative Writing Club", "Digital Photography Basics");
    }

}
