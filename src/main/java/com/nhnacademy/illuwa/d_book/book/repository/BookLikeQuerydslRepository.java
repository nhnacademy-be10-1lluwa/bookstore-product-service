package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookLikeQuerydslRepository {
    List<Book> getLikedBooksByMember(Long memberId);
}
