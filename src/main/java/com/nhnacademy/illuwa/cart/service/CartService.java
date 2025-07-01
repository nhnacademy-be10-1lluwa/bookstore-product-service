package com.nhnacademy.illuwa.cart.service;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.entity.Cart;

public interface CartService {

//    // 회원 번호로 카트 조회 -> 없으면 생성
//    Cart getOrCreateCart(Long memberId);
//
//    // 도서를 추가하거나 기존 항목 수량 합산 -> 제구 부족시 오류
//    BookCart addItem(Long memberId, Long bookId, int amount);
//
//    // 수량 변경
//    BookCart updateItem(Long memberId, Long bookId, int amount);
//
//    // 특정 책 제거
//    void removeItem(Long memberId, Long bookId);
//
//    // 카트 내 모든 항목 제거
//    List<BookCart> getCartItems(Long memberId);
//
//    // 결제 후 장바구니 비우기
//    void cleanCart(Long memberId);

    // 장바구니 조회
    Cart getOrCreateCart(CartRequest request);

    // 장바구니 내용 조회
    CartResponse getCart(CartRequest Request);

    // 도서 추가
    BookCartResponse addBook(BookCartRequest request);

    // 도서 수량 수정
    BookCartResponse updateBookCart(BookCartRequest request);

    //당일 ㅈ
    void removeBookCart(BookCartRequest request);

    void cleanCart(CartRequest request);
}
