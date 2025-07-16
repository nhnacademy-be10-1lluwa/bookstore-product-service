package com.nhnacademy.illuwa.d_book.book.service;


import com.nhnacademy.illuwa.d_book.book.dto.response.BookmarkResponse;

public interface BookmarkService {
    BookmarkResponse toggleBookmark(Long bookId, Long memberId);
}
