package com.backend.elasticsearchcourses.dto;

import java.util.List;

public record SearchResponseDto(
        long total,
        List<CourseDto> courses
) {}
