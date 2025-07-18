package com.nhnacademy.illuwa.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.service.CartService;
import com.nhnacademy.illuwa.d_book.book.controller.BookController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private long testMemberId;
    private long testBookId;
    private int testAmount;

    // 메서드 실행 전 곹옹적으로 필요한 설정
    @BeforeEach
    void setUp() {
        testMemberId = 1L;
        testBookId = 100L;
        testAmount = 2;
    }

    @Test
    @DisplayName("장바구니 조회 /api/cart")
    void getCart_success() throws Exception {
        List<BookCartResponse> testBookCartResponseList = Collections.emptyList();

        CartResponse expectedResponse = new CartResponse(1L, testBookCartResponseList, BigDecimal.valueOf(0));

        Mockito.when(cartService.getCart(any(CartRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/api/cart")
                        .header("X-USER-ID", testMemberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(0.0));

        Mockito.verify(cartService, Mockito.times(1)).getCart(any(CartRequest.class));
    }

    @Test
    @DisplayName("장바구니 도서 상품 추가 성공")
    void addBook_success() throws Exception {
        BookCartRequest testRequest = new BookCartRequest(null, testBookId, testAmount);

        // booid, title, amount, salePrice, imgUrl
        BookCartResponse testResponse =
                new BookCartResponse(
                        testBookId,
                        "Test Book Title",
                        testAmount,
                        BigDecimal.valueOf(1000),
                        "http://exple.com/img.jpg");

        Mockito.when(cartService.addBook(any(BookCartRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/cart/book")
                        .header("X-USER-ID", testMemberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(testBookId))
                .andExpect(jsonPath("$.amount").value(testAmount))
                .andExpect(jsonPath("$.title").value("Test Book Title"));

        Mockito.verify(cartService, Mockito.times(1)).addBook(any(BookCartRequest.class));
    }

    @Test
    @DisplayName("장바구니 도서 상품 수량 업데이트 성공")
    void updateBook_success() throws Exception {
        BookCartRequest testRequest = new BookCartRequest(null, testBookId, testAmount + 1);

        BookCartResponse testResponse = new BookCartResponse(testBookId, "Test Book Title", testAmount + 1, BigDecimal.valueOf(1000), null);

        Mockito.when(cartService.updateBookCart(any(BookCartRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/api/cart/books")
                        .header("X-USER-ID", testMemberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(testBookId))
                .andExpect(jsonPath("$.amount").value(testAmount + 1));

        Mockito.verify(cartService, Mockito.times(1)).updateBookCart(any(BookCartRequest.class));
    }

    @Test
    @DisplayName("장바구니 도서 개별 삭제 성공")
    void removeBook_success() throws Exception {
        BookCartRequest testRequest = new BookCartRequest(null, testBookId, 0);

        Mockito.doNothing().when(cartService).removeBookCart(any(BookCartRequest.class));

        mockMvc.perform(delete("/api/cart/book")
                        .header("X-USER-ID", testMemberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testRequest)))
                .andExpect(status().isOk());

        Mockito.verify(cartService, Mockito.times(1)).removeBookCart(any(BookCartRequest.class));
    }

    @Test
    @DisplayName("장바구니 목록 전체 삭제 성공")
    void clearCart_success() throws Exception {
        Mockito.doNothing().when(cartService).cleanCart(any(CartRequest.class));

        mockMvc.perform(delete("/api/cart")
                .header("X-USER-ID", testMemberId))
                .andExpect(status().isOk());

        Mockito.verify(cartService, Mockito.times(1)).cleanCart(any(CartRequest.class));
    }
}
