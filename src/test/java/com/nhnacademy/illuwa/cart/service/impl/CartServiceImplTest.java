package com.nhnacademy.illuwa.cart.service.impl;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.entity.BookCart;
import com.nhnacademy.illuwa.cart.entity.Cart;
import com.nhnacademy.illuwa.cart.exception.BookCartNotFoundException;
import com.nhnacademy.illuwa.cart.exception.InsufficientStockException;
import com.nhnacademy.illuwa.cart.repository.BookCartRepository;
import com.nhnacademy.illuwa.cart.repository.CartRepository;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock CartRepository cartRepository;
    @Mock BookCartRepository bookCartRepository;
    @Mock BookRepository bookRepository;

    @InjectMocks CartServiceImpl cartService;

    final Long memberId = 1L;
    final Long bookId   = 2L;

    @BeforeEach
    void setUp(){}

    @Test
    @DisplayName("getOrCreateCart: 존재하지 않으면 생성")
    void whenNoCart_thenCreate() {
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
        Cart saved = new Cart(memberId);
        when(cartRepository.save(any(Cart.class))).thenReturn(saved);

        Cart result = cartService.getOrCreateCart(new CartRequest(memberId));

        verify(cartRepository).save(any(Cart.class));
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    @DisplayName("getOrCreateCart: 이미 있으면 조회")
    void whenCartExists_thenReturn() {
        Cart existing = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(existing));

        Cart result = cartService.getOrCreateCart(new CartRequest(memberId));

        verify(cartRepository, never()).save(any());
        assertSame(existing, result);
    }

    @Test
    @DisplayName("getCart: 비어 있으면 items 빈 리스트")
    void testGetCart_empty() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(Collections.emptyList());

        CartResponse resp = cartService.getCart(new CartRequest(memberId));

        assertNotNull(resp);
        assertTrue(resp.getBooks().isEmpty());
    }

    @Test
    @DisplayName("getCart: items 정상 조회")
    void testGetCart_withItems() {
        Cart cart = new Cart(memberId);
        Book book = new Book(); book.setTitle("T"); book.setBookExtraInfo(new BookExtraInfo());
        BookCart bc1 = new BookCart(cart, book, 2);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(List.of(bc1));

        CartResponse resp = cartService.getCart(new CartRequest(memberId));

        assertEquals(1, resp.getBooks().size());
        assertEquals(book.getTitle(), resp.getBooks().get(0).getTitle());
    }

    @Test
    @DisplayName("addBook: 책 없음 -> NotFoundBookException")
    void testAddBook_bookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookException.class,
                () -> cartService.addBook(new BookCartRequest(memberId, bookId, 1))
        );
    }

    @Test
    @DisplayName("addBook: 재고 부족 -> InsufficientStockException")
    void testAddBook_insufficientStock() {
        Book book = new Book(); book.setBookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 0));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addBook(new BookCartRequest(memberId, bookId, 1))
        );
    }

    @Test
    @DisplayName("addBook: 새 항목 정상 추가")
    void testAddBook_new() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        book.setId(bookId);
        BookExtraInfo info = new BookExtraInfo(); info.setCount(10);
        book.setBookExtraInfo(info);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());
        when(bookCartRepository.save(any(BookCart.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookCartResponse resp = cartService.addBook(new BookCartRequest(memberId, bookId, 3));

        assertEquals(bookId, resp.getBookId());
        assertEquals(3,     resp.getAmount());
    }

    @Test
    @DisplayName("addBook: 기존 항목 있으면 수량 누적")
    void testAddBook_existing() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        book.setId(bookId);
        BookExtraInfo info = new BookExtraInfo(); info.setCount(10);
        book.setBookExtraInfo(info);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart existing = new BookCart(cart, book, 2);
        when(bookCartRepository.findByCartAndBook(cart, book))
                .thenReturn(Optional.of(existing));

        BookCartResponse resp = cartService.addBook(new BookCartRequest(memberId, bookId, 3));

        assertEquals(5, resp.getAmount());
        assertSame(bookId, resp.getBookId());
    }

    @Test
    @DisplayName("updateBookCart: 책 없으면 NotFoundBookException")
    void testUpdateBookCart_notFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookException.class,
                () -> cartService.updateBookCart(new BookCartRequest(memberId, bookId, 1))
        );
    }

    @Test
    @DisplayName("updateBookCart: 장바구니에 없으면 BookCartNotFoundException")
    void testUpdateBookCart_noCartEntry() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        Book book = new Book(); book.setBookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 5));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCartRepository.findByCartAndBook(cart, book))
                .thenReturn(Optional.empty());

        assertThrows(BookCartNotFoundException.class,
                () -> cartService.updateBookCart(new BookCartRequest(memberId, bookId, 2))
        );
    }

    @Test
    @DisplayName("updateBookCart: 수량 0이면 삭제")
    void testUpdateBookCart_delete() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        Book book = new Book(); book.setBookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 5));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart bc = new BookCart(cart, book, 2);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(bc));

        BookCartResponse resp = cartService.updateBookCart(new BookCartRequest(memberId, bookId, 0));

        verify(bookCartRepository).delete(bc);
        assertEquals(0, resp.getAmount());
    }

    @Test
    @DisplayName("removeBookCart: 책 없으면 NotFoundBookException")
    void testRemoveBookCart_notFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookException.class,
                () -> cartService.removeBookCart(new BookCartRequest(memberId, bookId, 1))
        );
    }

    @Test
    @DisplayName("removeBookCart: 항목 없으면 BookCartNotFoundException")
    void testRemoveBookCart_noEntry() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        Book book = new Book(); book.setBookExtraInfo(new BookExtraInfo());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCartRepository.findByCartAndBook(cart, book))
                .thenReturn(Optional.empty());

        assertThrows(BookCartNotFoundException.class,
                () -> cartService.removeBookCart(new BookCartRequest(memberId, bookId, 1))
        );
    }

    @Test
    @DisplayName("removeBookCart: 정상 삭제")
    void testRemoveBookCart_success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));
        Book book = new Book(); book.setBookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 5));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart bc = new BookCart(cart, book, 3);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(bc));

        cartService.removeBookCart(new BookCartRequest(memberId, bookId, 1));

        verify(bookCartRepository).delete(bc);
    }

    @Test
    @DisplayName("cleanCart: 모든 항목 삭제")
    void testCleanCart() {

        Book book1 = new Book(); book1.setBookExtraInfo(new BookExtraInfo());
        BookCart item1 = new BookCart(new Cart(memberId), book1, 1);
        Book book2 = new Book(); book2.setBookExtraInfo(new BookExtraInfo());
        BookCart item2 = new BookCart(new Cart(memberId), book2, 2);

        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(List.of(item1, item2));

        cartService.cleanCart(new CartRequest(memberId));

        verify(bookCartRepository).deleteAll(List.of(item1, item2));
    }
}
