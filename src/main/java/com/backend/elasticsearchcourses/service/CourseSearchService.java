package com.backend.elasticsearchcourses.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.backend.elasticsearchcourses.document.CourseDocument;
import com.backend.elasticsearchcourses.dto.CourseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSearchService {

    private final ElasticsearchClient client;

    public Page<CourseDto> searchCourses(
            String keyword,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            String startDate,
            String sort,
            int page,
            int size
    ) throws IOException {

        Pageable pageable = PageRequest.of(page, size);
        List<Query> filterClauses = new ArrayList<>();

        if (maxAge != null) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .untyped(u -> {
                        u.field("minAge");
                        u.lte(JsonData.of(maxAge));
                        return u;
                    })
            )));
        }

        if (minAge != null) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .untyped(u -> {
                        u.field("maxAge");
                        u.gte(JsonData.of(minAge));
                        return u;
                    })
            )));
        }

        if (minPrice != null || maxPrice != null) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .untyped(u -> {
                        u.field("price");
                        if (minPrice != null) u.gte(JsonData.of(minPrice));
                        if (maxPrice != null) u.lte(JsonData.of(maxPrice));
                        return u;
                    })
            )));
        }

        if (StringUtils.hasText(startDate)) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .untyped(u -> {
                        u.field("nextSessionDate");
                        u.gte(JsonData.of(startDate));
                        return u;
                    })
            )));
        }

        if (StringUtils.hasText(category)) {
            filterClauses.add(Query.of(q -> q.term(t -> t.field("category").value(category))));
        }
        if (StringUtils.hasText(type)) {
            filterClauses.add(Query.of(q -> q.term(t -> t.field("type").value(type))));
        }

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (StringUtils.hasText(keyword)) {
            boolQueryBuilder.must(q -> q
                    .multiMatch(mm -> mm
                            .fields("title", "description")
                            .query(keyword)
                    )
            );
        }

        if (!filterClauses.isEmpty()) {
            boolQueryBuilder.filter(filterClauses);
        }

        Query finalQuery = boolQueryBuilder.build()._toQuery();

        if (!StringUtils.hasText(keyword) && filterClauses.isEmpty()) {
            finalQuery = Query.of(q -> q.matchAll(m -> m));
        }

        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                .index("courses")
                .query(finalQuery)
                .from(pageable.getPageNumber() * pageable.getPageSize())
                .size(pageable.getPageSize());

        // Sorting
        applySorting(searchRequestBuilder, sort);

        log.info("Executing Elasticsearch Query: {}", finalQuery.toString());

        // Execute search
        SearchResponse<CourseDocument> response = client.search(searchRequestBuilder.build(), CourseDocument.class);

        // Convert hits to CourseDto
        List<CourseDto> results = getCourseDtos(response);
        long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;

        // Return a Page object which includes the total hits
        return new PageImpl<>(results, pageable, totalHits);
    }

    private void applySorting(SearchRequest.Builder builder, String sort) {
        String sortField;
        SortOrder sortOrder;

        if ("priceAsc".equalsIgnoreCase(sort)) {
            sortOrder = SortOrder.Asc;
            sortField = "price";
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            sortField = "price";
            sortOrder = SortOrder.Desc;
        } else {
            sortField = "nextSessionDate";
            sortOrder = SortOrder.Asc;
        }

        builder.sort(s -> s.field(f -> f.field(sortField).order(sortOrder)));
    }


    private List<CourseDto> getCourseDtos(SearchResponse<CourseDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(doc -> new CourseDto(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getCategory(),
                        doc.getPrice(),
                        doc.getNextSessionDate() != null ? Instant.parse(doc.getNextSessionDate()) : null
                ))
                .collect(Collectors.toList());
    }
}

