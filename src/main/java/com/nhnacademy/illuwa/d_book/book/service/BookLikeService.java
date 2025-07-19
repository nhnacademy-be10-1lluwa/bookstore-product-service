package com.nhnacademy.illuwa.d_book.book.service;


import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookLikeResponse;

import java.util.List;

public interface BookLikeService {
    BookLikeResponse toggleBookLikes(Long bookId, Long memberId);

    List<BookDetailResponse> getLikedBooksByMember(Long memberId);
}
