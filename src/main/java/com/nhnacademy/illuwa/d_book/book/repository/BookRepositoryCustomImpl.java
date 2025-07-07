package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.QBook;
import com.nhnacademy.illuwa.d_book.category.entity.QBookCategory;
import com.nhnacademy.illuwa.d_book.tag.entity.QBookTag;
import com.nhnacademy.illuwa.d_book.tag.entity.QTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BookRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Book> findBooksByCriteria(Long categoryId, String tagName, Pageable pageable) {
        QBook book = QBook.book;
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookTag bookTag = QBookTag.bookTag;
        QTag tag = QTag.tag;

        JPAQuery<Book> query = queryFactory.select(book).distinct().from(book);


        List<BooleanExpression> conditions = new ArrayList<>();


        if (categoryId != null) {

            query.join(bookCategory).on(bookCategory.book.id.eq(book.id));

            conditions.add(bookCategory.category.id.eq(categoryId));
        }


        if (StringUtils.hasText(tagName)) {

            query.join(bookTag).on(bookTag.book.id.eq(book.id));

            query.join(tag).on(bookTag.tag.id.eq(tag.id));

            conditions.add(tag.name.eq(tagName));
        }

        query.where(conditions.toArray(new BooleanExpression[0]));


        List<Book> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = queryFactory.select(book.countDistinct()).from(book);
        if (categoryId != null) {
            countQuery.join(bookCategory).on(bookCategory.book.id.eq(book.id));
        }
        if (StringUtils.hasText(tagName)) {
            countQuery.join(bookTag).on(bookTag.book.id.eq(book.id));
            countQuery.join(tag).on(bookTag.tag.id.eq(tag.id));
        }
        countQuery.where(conditions.toArray(new BooleanExpression[0]));

        long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}