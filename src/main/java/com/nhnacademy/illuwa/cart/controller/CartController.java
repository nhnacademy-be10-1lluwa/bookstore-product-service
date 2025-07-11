package com.nhnacademy.illuwa.cart.controller;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@RequestHeader("X-USER-ID") long memberId) {
        CartRequest cartRequest = new CartRequest(memberId);
        return cartService.getCart(cartRequest);
    }

    @PostMapping("/book")
    public BookCartResponse addBookCart(@RequestHeader("X-USER-ID") long memberId,
                                        @RequestBody BookCartRequest request){
        log.info("Received request to add book to cart. X-USER-ID: {}, BookId: {}, Amount: {}", memberId, request.getBookId(), request.getAmount());
        request.setMemberId(memberId);
        return cartService.addBook(request);
    }

    @PutMapping("/books")
    public BookCartResponse updateBook(@RequestHeader("X-USER-ID") long memberId,
                                       @RequestBody BookCartRequest request) {
        request.setMemberId(memberId);
        return cartService.updateBookCart(request);
    }

    @DeleteMapping("/book")
    public void removeBook(@RequestHeader("X-USER-ID") long memberId,
                           @RequestBody BookCartRequest request) {
        request.setMemberId(memberId);
        cartService.removeBookCart(request);
    }

    @DeleteMapping
    public void clearCart(@RequestHeader("X-USER-ID") long memberId) {
        cartService.cleanCart(new CartRequest(memberId));
    }
}
