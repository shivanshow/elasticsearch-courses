package com.backend.elasticsearchcourses;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.backend.elasticsearchcourses.document.CourseDocument;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;

public class ElasticsearchTestUtils {

    public static ElasticsearchClient createClient(ElasticsearchContainer container) {
        RestClient restClient = RestClient.builder(HttpHost.create(container.getHttpHostAddress()))
                .build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    public static List<CourseDocument> sampleSubset() {
        return List.of(
                CourseDocument.builder()
                        .id("1")
                        .title("Introduction to Algebra")
                        .description("Learn the basics of algebraic expressions and equations")
                        .category("Math")
                        .type("COURSE")
                        .price(120.5)
                        .build(),

                CourseDocument.builder()
                        .id("2")
                        .title("Creative Writing Club")
                        .description("Weekly club to explore poetry and short stories")
                        .category("Art")
                        .type("CLUB")
                        .price(40.0)
                        .build(),

                CourseDocument.builder()
                        .id("3")
                        .title("Robotics 101")
                        .description("Hands-on introduction to robotics and automation")
                        .category("Technology")
                        .type("COURSE")
                        .price(200.0)
                        .build(),

                CourseDocument.builder()
                        .id("4")
                        .title("Public Speaking Skills")
                        .description("Develop confidence in speaking in front of an audience")
                        .category("Communication")
                        .type("COURSE")
                        .price(95.0)
                        .build(),

                CourseDocument.builder()
                        .id("5")
                        .title("Geometry Essentials")
                        .description("Understand shapes, theorems, and proofs in geometry")
                        .category("Math")
                        .type("COURSE")
                        .price(115.0)
                        .build(),

                CourseDocument.builder()
                        .id("6")
                        .title("Digital Photography Basics")
                        .description("Learn how to capture and edit stunning photos")
                        .category("Art")
                        .type("COURSE")
                        .price(150.0)
                        .build(),

                CourseDocument.builder()
                        .id("7")
                        .title("Python for Beginners")
                        .description("Introduction to programming with Python")
                        .category("Technology")
                        .type("COURSE")
                        .price(180.0)
                        .build(),

                CourseDocument.builder()
                        .id("8")
                        .title("Advanced Algebra")
                        .description("Dive deeper into algebra with complex equations")
                        .category("Math")
                        .type("COURSE")
                        .price(140.0)
                        .build()
        );
    }
}
