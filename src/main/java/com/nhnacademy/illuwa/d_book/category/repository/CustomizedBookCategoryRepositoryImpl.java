package com.nhnacademy.illuwa.d_book.category.repository;

import com.nhnacademy.illuwa.d_book.category.entity.QBookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomizedBookCategoryRepositoryImpl implements CustomizedBookCategoryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findCategoryNamesByBookId(Long bookId) {

        QBookCategory bookCategory = QBookCategory.bookCategory;
        QCategory category = QCategory.category;

        return queryFactory
                .select(category.categoryName)
                .from(bookCategory)
                .join(bookCategory.category)
                .where(bookCategory.book.id.eq(bookId))
                .fetch();
    }

}