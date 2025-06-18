package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.BookAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.mapper.BookExternalMapper;
import com.nhnacademy.illuwa.d_book.book.mapper.BookResponseMapper;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {
    private final AladinBookApiService aladinBookApiService;
    private final BookRepository bookRepository;
    private final BookExternalMapper bookExternalMapper;
    private final BookResponseMapper bookResponseMapper;


    public BookService(AladinBookApiService aladinBookApiService, BookRepository bookRepository, BookExternalMapper bookExternalMapper, BookResponseMapper bookResponseMapper) {
        this.aladinBookApiService = aladinBookApiService;
        this.bookRepository = bookRepository;
        this.bookExternalMapper = bookExternalMapper;
        this.bookResponseMapper = bookResponseMapper;
    }

    //도서 등록 전 도서 검색
    public List<BookExternalResponse> searchBookFromExternalApi(String title) {
        List<BookExternalResponse> bookExternalResponseDtos = aladinBookApiService.searchBooksByTitle(title);

        if (bookExternalResponseDtos == null || bookExternalResponseDtos.isEmpty()) {
            throw new NotFoundBookException("제목과 일치하는 도서가 존재하지 않습니다.");
        }
        return bookExternalResponseDtos;
    }

    //등록된 도서 삭제 전 검색
    public void deleteBookByIsbn(String isbn){
        Optional<Book> byIsbn = bookRepository.findByIsbn(isbn);

        if(byIsbn.isEmpty()){
            log.info("도서를 찾을 수 없습니다. isbn : {}",isbn);
            throw new NotFoundBookException("isbn : " + isbn + "의 도서를 찾을 수 없습니다.");
        }
        bookRepository.deleteByIsbn(isbn);
    }


    // 피드백 -> fetch 부분 메서드 분리 예정
    public BookDetailResponse registerBook(String isbn) {
        //이미 등록된 도서인 경우
        log.info("도서 등록 시작: ISBN={}", isbn);
        if (bookRepository.existsByIsbn(isbn)) {
            log.warn("이미 등록된 도서: ISBN={}", isbn);
            throw new BookAlreadyExistsException("이미 등록된 도서입니다.");
        }

        BookExternalResponse bookByIsbn = aladinBookApiService.findBookByIsbn(isbn);

        if (bookByIsbn == null) {
            throw new NotFoundBookException("ISBN과 일치하는 도서가 없습니다.");
        }

        Book bookEntity = bookExternalMapper.toBookEntity(bookByIsbn);
        bookRepository.save(bookEntity);
        log.info("도서 등록 완료 : ID={}, ISBN={}", bookEntity.getId(),isbn);

        // mapper 필요

        return bookResponseMapper.toBookDetailResponse(bookEntity);
    }

}
