package com.nhnacademy.illuwa.search.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.repository.BookSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private BookSearchRepository bookSearchRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    @Test
    @DisplayName("키워드로 책 검색")
    void searchByKeyword() {
        SearchHits<BookDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(searchHits.getSearchHits()).thenReturn(Collections.emptyList());
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(BookDocument.class))).thenReturn(searchHits);

        Page<BookDocument> result = bookSearchService.searchByKeyword("test", PageRequest.of(0, 10));

        assertNotNull(result);
        verify(elasticsearchOperations).search(any(NativeQuery.class), eq(BookDocument.class));
    }

    @Test
    @DisplayName("카테고리로 책 검색")
    void searchByCategory() {
        SearchHits<BookDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(searchHits.getSearchHits()).thenReturn(Collections.emptyList());
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(BookDocument.class))).thenReturn(searchHits);

        Page<BookDocument> result = bookSearchService.searchByCategory("test", PageRequest.of(0, 10));

        assertNotNull(result);
        verify(elasticsearchOperations).search(any(NativeQuery.class), eq(BookDocument.class));
    }

    @Test
    @DisplayName("책 Elasticsearch에 동기화")
    void syncBookToElasticsearch() {
        Book book = new Book();
        book.setBookCategories(Collections.emptyList());
        book.setBookTags(Collections.emptySet());

        bookSearchService.syncBookToElasticsearch(book);

        verify(bookSearchRepository).save(any(BookDocument.class));
    }

    @Test
    @DisplayName("전체 책 Elasticsearch에 동기화")
    void syncAllBooksToElasticsearch() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(new Book()));

        bookSearchService.syncAllBooksToElasticsearch();

        verify(bookRepository).findAll();
        verify(bookSearchRepository).saveAll(any());
    }

    @Test
    @DisplayName("책 문서 저장")
    void save() {
        BookDocument document = new BookDocument();
        bookSearchService.save(document);
        verify(bookSearchRepository).save(document);
    }

    @Test
    @DisplayName("ID로 책 문서 삭제")
    void deleteById() {
        bookSearchService.deleteById(1L);
        verify(bookSearchRepository).deleteById(1L);
    }
}