package com.backend.elasticsearchcourses.config;

import com.backend.elasticsearchcourses.document.CourseDocument;
import com.backend.elasticsearchcourses.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final CourseRepository courseRepository;

    public DataLoader(ObjectMapper objectMapper, CourseRepository courseRepository) {
        this.objectMapper = objectMapper;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        InputStream is = getClass().getResourceAsStream("/sample-courses.json");
        if(is == null){
            throw new FileNotFoundException("sample-courses.json not found in resources");
        }
        List<CourseDocument> courses = objectMapper.readValue(
                is,
                new TypeReference<List<CourseDocument>>() {}
        );

        courses.forEach(course ->
                course.setSuggest(new Completion(new String[]{course.getTitle()}))
        );

        courseRepository.saveAll(courses);
        System.out.println("Loaded " + courses.size() + " courses into Elasticsearch");
    }
}
