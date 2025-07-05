package com.nhnacademy.illuwa.d_book.book.repository;


import com.nhnacademy.illuwa.d_book.book.document.BookDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, Long> {
}
