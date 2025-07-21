package com.nhnacademy.illuwa.d_book.book.repository.Impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.QBook;
import com.nhnacademy.illuwa.d_book.book.entity.QBookLike;
import com.nhnacademy.illuwa.d_book.book.repository.BookLikeQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookLikeQuerydslRepositoryImpl implements BookLikeQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    QBookLike bookLike = QBookLike.bookLike;
    QBook book = QBook.book;

    @Override
    public Page<Book> getLikedBooksByMember(Long memberId, Pageable pageable) {
        // 콘텐츠 조회
        List<Book> content = queryFactory
                .select(book)
                .from(bookLike)
                .join(bookLike.book, book)
                .where(bookLike.memberId.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(bookLike.count())
                .from(bookLike)
                .where(bookLike.memberId.eq(memberId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
