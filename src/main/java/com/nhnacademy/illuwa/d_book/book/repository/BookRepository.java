package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    List<Book> findByTitleContaining(String title);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {
            "bookImages",
            "bookTags.tag",
            "bookCategories.category"
    })
    Page<Book> findAll(Pageable pageable);

}
