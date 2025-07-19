package com.nhnacademy.illuwa.d_book.book.repository.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.QBook;
import com.nhnacademy.illuwa.d_book.book.entity.QBookLike;
import com.nhnacademy.illuwa.d_book.book.repository.BookLikeQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookLikeQuerydslRepositoryImpl implements BookLikeQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    QBookLike bookLike = QBookLike.bookLike;
    QBook book = QBook.book;

    @Override
    public List<Book> getLikedBooksByMember(Long memberId){
        return queryFactory
                .select(book)
                .from(bookLike)
                .join(bookLike.book, book)
                .where(bookLike.memberId.eq(memberId))
                .fetch();
    }
}
