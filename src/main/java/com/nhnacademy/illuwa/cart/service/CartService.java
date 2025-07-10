package com.nhnacademy.illuwa.cart.service;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.entity.BookCart;
import com.nhnacademy.illuwa.cart.entity.Cart;

import java.util.List;

public interface CartService {

    // 장바구니 조회
    Cart getOrCreateCart(CartRequest request);

    // 장바구니 내용 조회
    CartResponse getCart(CartRequest request);

    // 도서 추가
    BookCartResponse addBook(BookCartRequest request);

    // 도서 수량 수정
    BookCartResponse updateBookCart(BookCartRequest request);

    void removeBookCart(BookCartRequest request);

    void cleanCart(CartRequest request);

}
