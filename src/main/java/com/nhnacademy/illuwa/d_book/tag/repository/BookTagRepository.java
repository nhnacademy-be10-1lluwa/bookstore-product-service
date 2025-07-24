package com.nhnacademy.illuwa.d_book.tag.repository;

import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
    @EntityGraph(attributePaths = {"tag"})
    List<BookTag> findByBookId(Long bookId);

    boolean existsByBookIdAndTagId(Long bookId, Long tagId);

    @EntityGraph(attributePaths = {"tag"})
    List<BookTag> findByTagId(Long tagId);

    void deleteByBookIdAndTagId(Long bookId, Long tagId);
}
