package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long>, BookLikeQuerydslRepository {
    boolean existsByBook_IdAndMemberId(Long bookId, Long memberId);

    void deleteByBook_IdAndMemberId(Long bookId, Long memberId);
}