package com.backend.elasticsearchcourses.repository;

import com.backend.elasticsearchcourses.document.CourseDocument;
import com.backend.elasticsearchcourses.dto.CourseDto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {
}
