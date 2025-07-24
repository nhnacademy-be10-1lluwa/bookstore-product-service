package com.nhnacademy.illuwa.cart.controller;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "장바구니 API", description = "장바구니와 관련된 API 입니다.")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "로그인 유저의 장바구니 생성 및 조회", description = "로그인한 유저의 장바구니가 존재한다면 불러오고 없다면 만들어냅니다.")
    @ApiResponse(responseCode = "200", description = "로그인한 유저의 장바구니를 성공적으로 불러왔습니다.")
    public CartResponse getCart(@RequestHeader("X-USER-ID") long memberId) {
        CartRequest cartRequest = new CartRequest(memberId);
        return cartService.getCart(cartRequest);
    }

    @PostMapping("/book")
    @Operation(summary = "장바구니에 물품 추가", description = "유저의 장바구니에 삼품을 담습니다")
    @ApiResponse(responseCode = "200", description = "장바구니에 성공적으로 상품이 담겼습니다.")
    @ApiResponse(responseCode = "404", description = "해당 책은 존재하지 않습니다.")
    public BookCartResponse addBookCart(@RequestHeader("X-USER-ID") long memberId,
                                        @RequestBody BookCartRequest request){
        log.info("Received request to add book to cart. X-USER-ID: {}, BookId: {}, Amount: {}", memberId, request.getBookId(), request.getAmount());
        request.setMemberId(memberId);
        return cartService.addBook(request);
    }

    @PutMapping("/books")
    @Operation(summary = "장바구니에 담김 상품 갱신", description = "장바구니의 삼품을 갱신할 떄 사용합니다..")
    @ApiResponse(responseCode = "200", description = "장바구니의 삼품의 수량이 갱신되었습니다.")
    @ApiResponse(responseCode = "404", description = "해당 책은 존재하지 않습니다.")
    public BookCartResponse updateBook(@RequestHeader("X-USER-ID") long memberId,
                                       @RequestBody BookCartRequest request) {
        request.setMemberId(memberId);
        return cartService.updateBookCart(request);
    }

    @DeleteMapping("/book")
    @Operation(summary = "장바구니의 삼품 개별 삭제", description = "장바구니의 상품을 개별로 삭제할 떄 사용됩니다.")
    @ApiResponse(responseCode = "200", description = "장바구니의 삼품의 삭제가 성공했습니다.")
    @ApiResponse(responseCode = "404", description = "해당 책은 존재하지 않습니다..")
    public void removeBook(@RequestHeader("X-USER-ID") long memberId,
                           @RequestBody BookCartRequest request) {
        request.setMemberId(memberId);
        cartService.removeBookCart(request);
    }

    @DeleteMapping
    @Operation(summary = "장바구니 물품 전체 삭제", description = "장바구니의 상품이 전체 삭제 되었습니다.")
    @ApiResponse(responseCode = "200", description = "삼품이 전체적으로 삭제되었습니다.")
    public void clearCart(@RequestHeader("X-USER-ID") long memberId) {
        cartService.cleanCart(new CartRequest(memberId));
    }
}
