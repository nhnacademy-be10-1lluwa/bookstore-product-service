package com.nhnacademy.illuwa.d_book.book.repository.Impl;

import com.nhnacademy.illuwa.cart.entity.QBookCart;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.QBook;
import com.nhnacademy.illuwa.d_book.book.entity.QBookImage;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepositoryCustom;
import com.nhnacademy.illuwa.d_book.category.entity.QBookCategory;
import com.nhnacademy.illuwa.d_book.tag.entity.QBookTag;
import com.nhnacademy.illuwa.d_book.tag.entity.QTag;
import com.nhnacademy.illuwa.d_review.review.entity.QReview;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.nhnacademy.illuwa.d_review.review.entity.QReviewImage.reviewImage;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public BookRepositoryCustomImpl(EntityManager em, EntityManager em1) {
        this.queryFactory = new JPAQueryFactory(em);
        this.em = em1;
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


    @Transactional
    public void deleteBookAndRelatedEntities(Long bookId) {
        QBook book = QBook.book;
        QBookCart bookCart = QBookCart.bookCart;
        QReview review = QReview.review;
        QBookImage bookImage = QBookImage.bookImage;
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QBookTag bookTag = QBookTag.bookTag;

        // 자식 -> 부모 entity 순서 확인

        // 장바구니 삭제
        new JPADeleteClause(em, bookCart)
                .where(bookCart.book.id.eq(bookId))
                .execute();

        // 리뷰 이미지 삭제
        new JPADeleteClause(em, reviewImage)
                .where(reviewImage.review.book.id.eq(bookId))
                .execute();

        // 리뷰 삭제
        new JPADeleteClause(em, review)
                .where(review.book.id.eq(bookId))
                .execute();

        // 도서 이미지 삭제
        new JPADeleteClause(em, bookImage)
                .where(bookImage.book.id.eq(bookId))
                .execute();

        // 카테고리 매핑 삭제
        new JPADeleteClause(em, bookCategory)
                .where(bookCategory.book.id.eq(bookId))
                .execute();

        // 태그 매핑 삭제
        new JPADeleteClause(em, bookTag)
                .where(bookTag.book.id.eq(bookId))
                .execute();

        // 도서 삭제
        new JPADeleteClause(em, book)
                .where(book.id.eq(bookId))
                .execute();
    }

    @Override
    public List<Book> findBooksByCategories(List<Long> categoryIds) {

        QBook book = QBook.book;
        QBookCategory bookCategory = QBookCategory.bookCategory;

        return queryFactory
                // SELECT b
                .select(bookCategory.book)
                // FROM BookCategory bc
                .from(bookCategory)
                // JOIN bc.book b (BookCategory 엔티티의 book 필드를 통해 Book 엔티티와 조인)
                .join(bookCategory.book, book)
                // WHERE bc.category.id IN :categoryIds
                .where(bookCategory.category.id.in(categoryIds))
                .distinct() // 중복된 책이 조회될 수 있으므로 distinct() 추가
                .fetch();
    }
}