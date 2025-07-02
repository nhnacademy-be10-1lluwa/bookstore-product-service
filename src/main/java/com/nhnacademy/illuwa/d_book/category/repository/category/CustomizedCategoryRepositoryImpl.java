package com.nhnacademy.illuwa.d_book.category.repository.category;

import com.nhnacademy.illuwa.d_book.category.entity.Category;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


public class CustomizedCategoryRepositoryImpl extends QuerydslRepositorySupport implements CustomizedCategoryRepository{
    public CustomizedCategoryRepositoryImpl(){
        super(Category.class);
    }

}
