package com.nhnacademy.illuwa.search.service;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.repository.BookSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Slf4j
public class BookSearchService {

    private final BookSearchRepository bookSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BookRepository bookRepository;

    public BookSearchService(BookSearchRepository bookSearchRepository, ElasticsearchOperations elasticsearchOperations, BookRepository bookRepository) {
        this.bookSearchRepository = bookSearchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.bookRepository = bookRepository;
    }


    public Page<BookDocument> searchByKeyword(String keyword, Pageable pageable) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("title", "title.jaso", "title.icu", "description", "author")
                        )
                )
                .withPageable(pageable)
                .build();

        SearchHits<BookDocument> searchHits = elasticsearchOperations.search(query, BookDocument.class);

        List<BookDocument> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }


    public void syncBookToElasticsearch(Book book) {
        BookDocument bookDocument = BookDocument.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .salePrice(book.getSalePrice())
                .publishedDate(book.getPublishedDate())
                .thumbnailUrl(
                        book.getBookImages() != null && !book.getBookImages().isEmpty()
                                ? book.getBookImages().get(0).getImageUrl()
                                : null
                )
                .build();

        bookSearchRepository.save(bookDocument);
        log.info("Elasticsearch에 동기화 완료: id={}", book.getId());
    }


    public void save(BookDocument document) {
        bookSearchRepository.save(document);
    }

    public void deleteById(Long id) {
        bookSearchRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void syncAllBooksToElasticsearch() {
        List<Book> books = bookRepository.findAll();
        List<BookDocument> documents = books.stream()
                .map(book -> BookDocument.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .description(book.getDescription())
                        .author(book.getAuthor())
                        .publisher(book.getPublisher())
                        .isbn(book.getIsbn())
                        .salePrice(book.getSalePrice())
                        .publishedDate(book.getPublishedDate())
                        .thumbnailUrl(book.getBookImages() != null && !book.getBookImages().isEmpty()
                                ? book.getBookImages().get(0).getImageUrl()
                                : null)
                        .build())
                .toList();

        bookSearchRepository.saveAll(documents);
    }

}
