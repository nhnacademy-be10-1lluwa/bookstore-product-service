package com.nhnacademy.illuwa.d_book.bookCategory.repository;

import com.nhnacademy.illuwa.common.config.QuerydslConfig;
import com.nhnacademy.illuwa.common.enums.Status;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.category.entity.BookCategory;
import com.nhnacademy.illuwa.d_book.category.entity.Category;
import com.nhnacademy.illuwa.d_book.category.repository.bookcategory.BookCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class CustomizedBookCategoryRepositoryImplTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    private Long bookId;

    @BeforeEach
    void setUp(){
        Book book =new Book(
                null,
                "인어 공주",
                "",
                "인어 공주는...",
                "안데르센",
                "스웨덴출판사",
                LocalDate.of(2016,06,16),
                "123456789EE",
                15000,
                13000,
                "book/marmaid.jpeg",
                new BookExtraInfo(Status.DELETED,true)
        );
        entityManager.persist(book);
        this.bookId = book.getId();

        Category category1 = new Category("서양");
        Category category2 = new Category("문학");
        Category category3 = new Category("소설");


        category1.addChildCategory(category2);
        category2.addChildCategory(category3);
        entityManager.persist(category1);

        BookCategory bookCategory1 = new BookCategory(book,category1);
        BookCategory bookCategory2 = new BookCategory(book,category2);
        BookCategory bookCategory3 = new BookCategory(book,category3);

        entityManager.persist(bookCategory1);
        entityManager.persist(bookCategory2);
        entityManager.persist(bookCategory3);
    }


    @Test
    @DisplayName("BookId를 통해 해당 책의 Category name 출력")
    void findCategoryNamesByBookId_Success(){
        List<String> categoryNames = bookCategoryRepository.findCategoryNamesByBookId(this.bookId);

        assertThat(categoryNames).isNotNull();
        assertThat(categoryNames).hasSize(3);
        assertThat(categoryNames).containsExactlyInAnyOrder("서양","문학","소설");

    }


}
