package com.nhnacademy.illuwa.cart.repository;

import com.nhnacademy.illuwa.cart.entity.BookCart;
import com.nhnacademy.illuwa.cart.entity.Cart;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookCartRepository extends JpaRepository<BookCart, Long> {
    Optional<BookCart> findByCartAndBook(Cart cart, Book book);
    List<BookCart> findAllByCart_MemberId(Long memberId);


    @Modifying
    @Transactional
    void deleteAllByBook_Id(Long bookId);
}
