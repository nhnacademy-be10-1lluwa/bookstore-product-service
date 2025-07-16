package com.nhnacademy.illuwa.search.service;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
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


    public Page<BookDocument> searchByCategory(String categoryKeyword, Pageable pageable) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .term(t -> t
                                .field("categories")
                                .value(categoryKeyword)
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
        if (book.getBookCategories() == null || book.getBookCategories().isEmpty()) {
            throw new IllegalStateException("도서에 카테고리가 없습니다.");
        }

        List<String> categoryHierarchy = book.getBookCategories().stream()
                .flatMap(bc -> buildCategoryHierarchy(bc.getCategory()).stream())
                .distinct()
                .toList();


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
                .categories(categoryHierarchy)
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
                .map(book -> {
                    List<String> categoryHierarchy = new ArrayList<>();
                    if (book.getBookCategories() != null && !book.getBookCategories().isEmpty()) {
                        categoryHierarchy = book.getBookCategories().stream()
                                .flatMap(bc -> buildCategoryHierarchy(bc.getCategory()).stream())
                                .distinct()
                                .toList();
                    }

                    return BookDocument.builder()
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
                            .categories(categoryHierarchy)
                            .build();
                })
                .toList();

        bookSearchRepository.saveAll(documents);
    }

    public static  List<String> buildCategoryHierarchy(Category category) {
        List<String> hierarchy = new ArrayList<>();
        while (category != null) {
            hierarchy.add(category.getCategoryName());
            category = category.getParentCategory();
        }
        return hierarchy;
    }

}
