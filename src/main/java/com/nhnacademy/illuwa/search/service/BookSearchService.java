package com.nhnacademy.illuwa.search.service;


import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.search.document.BookDocument;
import com.nhnacademy.illuwa.search.repository.BookSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookSearchService {

    private final BookSearchRepository bookSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final BookRepository bookRepository;


    public Page<BookDocument> searchByKeyword(String keyword, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "_score");

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        // function_score 쿼리로 전체를 감쌉니다.
                        .functionScore(fs -> fs
                                // 1. 기본 쿼리: 먼저 키워드와 일치하는 모든 문서를 찾습니다.
                                .query(baseQuery -> baseQuery
                                        .multiMatch(m -> m
                                                .query(keyword)
                                                .fields("title", "title.jaso", "title.icu", "description", "author", "tags")
                                        )
                                )
                                .functions(
                                        new FunctionScore.Builder()
                                                .filter(f -> f.match(m -> m.field("title").query(keyword)))
                                                .weight(3.0)
                                                .build(),

                                        new FunctionScore.Builder()
                                                .filter(f -> f.match(m -> m.field("author").query(keyword)))
                                                .weight(2.0)
                                                .build(),

                                        new FunctionScore.Builder()
                                                .filter(f -> f.match(m -> m.field("tags").query(keyword)))
                                                .weight(1.5)
                                                .build()
                                )
                                .scoreMode(FunctionScoreMode.Multiply)
                                .boostMode(FunctionBoostMode.Sum)
                        )
                )
                .withPageable(pageable)
                .withSort(sort)
                .build();

        SearchHits<BookDocument> searchHits = elasticsearchOperations.search(query, BookDocument.class);

        searchHits.forEach(hit -> {
            log.info("도서 제목: {}, score: {}", hit.getContent().getTitle(), hit.getScore());
        });

        List<BookDocument> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

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


    @Async
    public void syncBookToElasticsearch(Book book) {
        BookDocument bookDocument = convertToDocument(book);
        bookSearchRepository.save(bookDocument);
        log.info("Elasticsearch에 동기화 완료: id={}", book.getId());
    }


    public void save(BookDocument document) {
        bookSearchRepository.save(document);
    }


    @Async
    public void deleteById(Long id) {
        bookSearchRepository.deleteById(id);
    }


    @Async
    @Transactional(readOnly = true)
    public void syncAllBooksToElasticsearch() {

        List<Book> books = bookRepository.findAll();


        List<BookDocument> documents = books.stream()
                .map(this::convertToDocument)
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

    public void addTagToBookDocument(Long bookId, String tagName){
        bookSearchRepository.findById(bookId).ifPresent(bookDocument -> {
            List<String> tags = bookDocument.getTags();
            if(tags == null) {
                tags = new ArrayList<>();
            }
            if(!tags.contains(tagName)){
                tags.add(tagName);
                bookDocument.setTags(tags);
                bookSearchRepository.save(bookDocument);
            }
        });
    }

    public void removeTagFromBookDocument(Long bookId, String tagName) {
        bookSearchRepository.findById(bookId).ifPresent(bookDocument -> {
            List<String> tags = bookDocument.getTags();
            if (tags != null && tags.contains(tagName)) {
                tags.remove(tagName);
                bookDocument.setTags(tags);
                bookSearchRepository.save(bookDocument);
            }
        });
    }

    private BookDocument convertToDocument(Book book) {
        if (book.getBookCategories() == null || book.getBookCategories().isEmpty()) {
            // 전체 동기화 시에는 예외를 던지는 대신 로그를 남기고 건너뛰는 것이 더 안정적일 수 있습니다.
            log.warn("도서에 카테고리가 없습니다. id={}", book.getId());
        }

        List<String> categoryHierarchy = new ArrayList<>();
        if (book.getBookCategories() != null) {
            categoryHierarchy = book.getBookCategories().stream()
                    .flatMap(bc -> buildCategoryHierarchy(bc.getCategory()).stream())
                    .distinct()
                    .toList();
        }

        List<String> tags = new ArrayList<>();
        if (book.getBookTags() != null) {
            tags = book.getBookTags().stream()
                    .map(bt -> bt.getTag().getName())
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
                .thumbnailUrl(
                        book.getBookImages() != null && !book.getBookImages().isEmpty()
                                ? book.getBookImages().get(0).getImageUrl()
                                : null
                )
                .categories(categoryHierarchy)
                .tags(tags)
                .build();
    }

}
