package com.nhnacademy.illuwa.d_book.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "categories")
@BatchSize(size = 100)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Category> childrenCategory = new ArrayList<>();

    @Column(nullable = false, unique = true, name = "category_name")
    private String categoryName;

    public Category(String categoryName){
        this.categoryName = categoryName;
    }

    public void addChildCategory(Category childCategory){
        childrenCategory.add(childCategory);
        childCategory.setParentCategory(this);
    }

}

