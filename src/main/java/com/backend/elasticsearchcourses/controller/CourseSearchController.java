package com.backend.elasticsearchcourses.controller;

import com.backend.elasticsearchcourses.dto.CourseDto;
import com.backend.elasticsearchcourses.dto.SearchResponseDto;
import com.backend.elasticsearchcourses.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;

    @GetMapping("/search")
    public ResponseEntity<SearchResponseDto> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<CourseDto> results = courseSearchService.searchCourses(
                    q, minAge, maxAge, category, type,
                    minPrice, maxPrice, startDate, sort, page, size
            );
            SearchResponseDto response = new SearchResponseDto(
                    results.getTotalElements(),
                    results.getContent()
            );
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

