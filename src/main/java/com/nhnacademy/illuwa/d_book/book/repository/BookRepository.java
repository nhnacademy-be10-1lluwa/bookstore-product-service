package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface BookRepository extends JpaRepository<Book,Long> {

//    List<Book> findByTitle(String title);

    List<Book> findByTitleContaining(String title);

    List<Book> findByDescriptionContaining(String description);

    List<Book> findByAuthor(String author);

    List<Book> findByPublisher(String publisher);

    List<Book> findByIsbn(String isbn);

    List<Book> findBySalePriceBetween(int min, int max);

    List<Book> findByPublishedDateTimeAfter(LocalDateTime dateTime);

    List<Book> findByPublishedDateTimeBefore(LocalDateTime dateTime);

    Page<Book> findByAuthor(String author, Pageable pageable);

    void deleteByIsbn(String isbn);

    boolean existsByIsbn(String isbn);



}
