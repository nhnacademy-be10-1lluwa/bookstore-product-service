package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    AladinBookApiService aladinBookApiService;

    BookService(AladinBookApiService aladinBookApiService){
        this.aladinBookApiService = aladinBookApiService;
    }

    //도서 등록 전 도서 검색
    public List<BookExternalResponse> searchBookFromExternalApi(String title){
        List<BookExternalResponse> bookExternalResponseDtos = aladinBookApiService.searchBooksByTitle(title);

        return bookExternalResponseDtos;
    }

}
