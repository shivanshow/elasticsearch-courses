package com.backend.elasticsearchcourses.dto;

import java.time.Instant;

public record CourseDto(
        String id,
        String title,
        String category,
        Double price,
        Instant nextSessionDate
) {}
