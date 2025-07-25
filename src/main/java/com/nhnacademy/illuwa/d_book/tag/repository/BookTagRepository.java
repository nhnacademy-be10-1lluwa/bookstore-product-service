package com.nhnacademy.illuwa.d_book.tag.repository;

import java.util.Optional;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    List<BookTag> findByBookId(Long bookId);

    boolean existsByBookIdAndTagId(Long bookId, Long tagId);

    List<BookTag> findByTagId(Long tagId);

    void deleteByBookIdAndTagId(Long bookId, Long tagId);

    Optional<BookTag> findByBookIdAndTagId(Long bookId, Long tagId);
}
