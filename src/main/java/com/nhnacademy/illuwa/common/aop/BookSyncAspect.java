package com.nhnacademy.illuwa.common.aop;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BookSyncAspect {

    private final BookSearchService bookSearchService;
    private final BookRepository bookRepository;

    // @BookRegisterSync가 붙은 메서드가 실행되고 나서(result 반환된 후)에 실행됨
    @AfterReturning(
            pointcut = "@annotation(com.nhnacademy.illuwa.common.aop.BookRegisterSync)",
            returning = "result"
    )
    public void syncToElasticsearch(Object result) {
        if (result instanceof BookDetailResponse response) {
            Long bookId = response.getId();
            log.info("도서 등록 완료 후 Elasticsearch 동기화 시작 (bookId={})", bookId);

            bookRepository.findById(bookId).ifPresent(bookSearchService::syncBookToElasticsearch);
        } else {
            log.warn("BookDetailResponse 타입 에러");
        }
    }
}