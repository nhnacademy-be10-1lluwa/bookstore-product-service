package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    @EntityGraph(attributePaths = {"bookImages", "bookTags", "bookCategories"})
    List<Book> findByTitleContaining(String title);

    @EntityGraph(attributePaths = {"bookImages", "bookTags", "bookCategories"})
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @EntityGraph(attributePaths = {"bookImages", "bookTags", "bookCategories"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"bookImages", "bookTags", "bookCategories"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"bookImages", "bookTags", "bookCategories"})
    List<Book> findAll();

}
