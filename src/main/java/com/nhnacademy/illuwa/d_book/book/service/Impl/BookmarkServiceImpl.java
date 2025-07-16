package com.nhnacademy.illuwa.d_book.book.service.Impl;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookmarkResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.Bookmark;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookmarkRepository;
import com.nhnacademy.illuwa.d_book.book.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookRepository bookRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    @Transactional
    public BookmarkResponse toggleBookmark(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new NotFoundBookException("도서를 찾을 수 없습니다." + "Book ID: " + bookId)
        );

        boolean isBookmarked = bookmarkRepository.existsByBook_IdAndMemberId(bookId, memberId);

        if(isBookmarked) {
            bookmarkRepository.deleteByBook_IdAndMemberId(bookId, memberId);
        } else {
            bookmarkRepository.save(Bookmark.of(book, memberId));
        }

        return new BookmarkResponse(!isBookmarked);
    }
}
