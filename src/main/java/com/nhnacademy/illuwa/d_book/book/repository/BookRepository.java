package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book,Long> {


    List<Book> findByTitleContaining(String title);

    List<Book> findByDescriptionContaining(String description);

    List<Book> findByAuthor(String author);

    List<Book> findByPublisher(String publisher);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findBySalePriceBetween(int min, int max);
    List<Book> findByPublishedDateAfter(LocalDate date);
    List<Book> findByPublishedDateBefore(LocalDate date);

    Page<Book> findByAuthor(String author, Pageable pageable);
    Page<Book> findByPublishedDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Book b WHERE b.isbn = :isbn")  // OK - 삭제
    int deleteByIsbn(@Param("isbn") String isbn);

    boolean existsByIsbn(String isbn);

    Page<Book> findAll(Pageable pageable);

}
