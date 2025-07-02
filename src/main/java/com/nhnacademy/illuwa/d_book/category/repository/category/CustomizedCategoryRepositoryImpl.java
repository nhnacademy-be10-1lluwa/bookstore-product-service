package com.nhnacademy.illuwa.d_book.category.repository.category;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.entity.QCategory;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Optional;

public class CustomizedCategoryRepositoryImpl extends QuerydslRepositorySupport implements CustomizedCategoryRepository{
    public CustomizedCategoryRepositoryImpl(){
        super(Category.class);
    }

}
