package com.nhnacademy.illuwa.cart.controller;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

//    @GetMapping
//    public CartResponse getCart(@PathVariable Long memberId){
//        return cartService.getCart(new CartRequest(memberId));
//    }

    @GetMapping
    public CartResponse getCart(@RequestHeader("X-USER-ID") long memberId) {
        CartRequest cartRequest = new CartRequest(memberId);
//        return cartService.getCart(new CartRequest(memberId));
        return cartService.getCart(cartRequest);
    }

    @PostMapping("/books")
    public BookCartResponse addBook(@RequestHeader("X-USER-ID") long memberId,
                                    @RequestBody BookCartRequest request){

        request.setMemberId(memberId);
        return cartService.addBook(request);
    }

    @PutMapping("/books")
    public BookCartResponse updateBook(@RequestHeader("X-USER-ID") long memberId,
                                       @RequestBody BookCartRequest request) {
        request.setMemberId(memberId);
        return cartService.updateBookCart(request);
    }

    @DeleteMapping("/books")
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
