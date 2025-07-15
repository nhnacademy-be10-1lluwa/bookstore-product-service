package com.nhnacademy.illuwa.search.repository;


import com.nhnacademy.illuwa.search.document.BookDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Pageable;


public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, Long> {

    Page<BookDocument> findByTitleContainingOrAuthorContainingOrDescriptionContaining(String title, String author, String description, Pageable pageable);
}
