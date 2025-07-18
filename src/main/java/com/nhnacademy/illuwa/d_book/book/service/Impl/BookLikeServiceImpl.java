package com.nhnacademy.illuwa.d_book.book.service.Impl;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookLikeResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookLike;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.book.repository.BookLikeRepository;
import com.nhnacademy.illuwa.d_book.book.service.BookLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookLikeServiceImpl implements BookLikeService {
    private final BookRepository bookRepository;
    private final BookLikeRepository bookLikeRepository;

    @Override
    @Transactional
    public BookLikeResponse toggleBookLikes(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new NotFoundBookException("도서를 찾을 수 없습니다." + "Book ID: " + bookId)
        );

        boolean isLiked = bookLikeRepository.existsByBook_IdAndMemberId(bookId, memberId);

        if(isLiked) {
            bookLikeRepository.deleteByBook_IdAndMemberId(bookId, memberId);
        } else {
            bookLikeRepository.save(BookLike.of(book, memberId));
        }

        return new BookLikeResponse(!isLiked);
    }
}
