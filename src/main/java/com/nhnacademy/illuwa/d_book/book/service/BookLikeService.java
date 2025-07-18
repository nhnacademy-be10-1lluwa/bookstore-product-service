package com.nhnacademy.illuwa.d_book.book.service;


import com.nhnacademy.illuwa.d_book.book.dto.response.BookLikeResponse;

public interface BookLikeService {
    BookLikeResponse toggleBookLikes(Long bookId, Long memberId);
}
