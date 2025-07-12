package com.nhnacademy.illuwa.d_book.tag.repository;

import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    List<BookTag> findByBookId(Long bookId);
}
