package com.nhnacademy.illuwa.cart.service.impl;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BookCartRepository bookCartRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private final Long memberId = 1L;
    private final Long bookId = 2L;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("장바구니 조회 및 생성 -> 생성")
    void testGetOrCreateCart_CreateNew() {
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.empty());
        Cart newCart = new Cart(memberId);

        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Cart result = cartService.getOrCreateCart(memberId);

        verify(cartRepository).save(any(Cart.class));
        assertEquals(memberId, result.getMemberId());

    }

    @Test
    @DisplayName("장바구니 조회 및 생성 -> 조회")
    void testGetOrCreateCart_ReturnExistingCart() {
        Cart newCart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(newCart));

        Cart result = cartService.getOrCreateCart(memberId);

        verify(cartRepository, never()).save(any());
        assertSame(newCart, result);
    }

    @Test
    @DisplayName("도서 추가 -> 해당 id에 존재하지 않는 책일 경우")
    void testAddItem_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        NotFoundBookException ex = assertThrows(NotFoundBookException.class,
                () -> cartService.addItem(memberId, bookId, 1));

        assertEquals("해당 ID에 적합한 책은 존재하지 않습니다.", ex.getMessage());
    }

    @Test
    @DisplayName(("도서 추가 -> 구매하려는 책의 수량이 존재하는 수량보다 많을 경우"))
    void testAddItem_InsufficientStock() {
        Book book = new Book();
        book.setBookExtraInfo(new BookExtraInfo(Status.NORMAL, false, 0));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class,
                () -> cartService.addItem(memberId, bookId, 1));
    }

    @Test
    @DisplayName("도서 추가 -> 새로운 도서 추가 성공")
    void testAddItem_NewItem_Success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();

        extraInfo.setCount(10);
        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.empty());

        when(bookCartRepository.save(any(BookCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookCart result = cartService.addItem(memberId, bookId, 3);

        assertEquals(10, book.getBookExtraInfo().getCount());
        assertEquals(3, result.getAmount());
        assertEquals(cart, result.getCart());
        assertEquals(book, result.getBook());
    }

    @Test
    @DisplayName("도서 추가 -> 이미 존재하는 책일 경우")
    void testAddItem_ExistingItem_Success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();
        extraInfo.setCount(10);
        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart existing = new BookCart(cart, book , 2);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(existing));

        BookCart result = cartService.addItem(memberId, bookId, 3);

        assertEquals(5, existing.getAmount());
        assertSame(existing, result);
    }


    @Test
    @DisplayName("수량 변경 -> 해당 ID의 책이 존재하지 않을 경우")
    void testUpdateItem_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        NotFoundBookException ex = assertThrows(NotFoundBookException.class,
                () -> cartService.updateItem(memberId, bookId, 1));

        assertEquals("해당 ID에 적합한 책을 발견하지 못했습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("수량 변경 -> 해당 ID의 책이 장바구니에 존재하지 않을 경우")
    void testUpdateItem_BookCartNotFound() {
        // 1) getOrCreateCart를 위한 stub
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId))
                .thenReturn(Optional.of(cart));

        // 2) 책 조회 stub
        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();
        extraInfo.setCount(1);               // 수량 1로 세팅
        book.setBookExtraInfo(extraInfo);

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        // 3) 장바구니에 책이 없다고 stub
        when(bookCartRepository.findByCartAndBook(eq(cart), eq(book)))
                .thenReturn(Optional.empty());

        // 실행 및 검증
        assertThrows(BookCartNotFoundException.class,
                () -> cartService.updateItem(memberId, bookId, 1));
    }

    @Test
    @DisplayName("수량 변경 -> 구매 수량이 증가 했을 떄 기존의 수량이 부족한 경우")
    void testUpdateItem_InsufficientStock() {

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();

        extraInfo.setCount(1);
        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThrows(InsufficientStockException.class,
                () -> cartService.updateItem(memberId, bookId, 5));
    }

    @Test
    @DisplayName("수량 변경 -> 수량 증가 성공")
    void testUpdateItem_Success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();
        extraInfo.setCount(10);

        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart bookCart = new BookCart(cart, book , 2);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(bookCart));

        BookCart result = cartService.updateItem(memberId, bookId, 5);

        assertEquals(10, book.getBookExtraInfo().getCount());
        assertEquals(5, result.getAmount());
    }

    @Test
    @DisplayName("수량 변경 -> 구매 수량이 0인 경우 항목에서 삭제")
    void testUpdateItem_DeleteItem() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();
        extraInfo.setCount(10);
        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart bookCart = new BookCart(cart, book , 2);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(bookCart));

        BookCart result = cartService.updateItem(memberId, bookId, 0);

        verify(bookCartRepository).delete(bookCart);
        assertEquals(10, book.getBookExtraInfo().getCount());
        assertEquals(0, result.getAmount());
    }

    @Test
    @DisplayName("특정 도서 제거 -> 해당 ID의 책이 없을 경우")
    void testRemoveItem_BookNotFound() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookException.class,
                () -> cartService.removeItem(memberId, bookId));
    }

    @Test
    @DisplayName("특정 도서 제거 -> 장바구니에 담긴 상품이 없을 경우")
    void testRemoveItem_BookCartNotFound() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        book.setBookExtraInfo(new BookExtraInfo());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookCartRepository.findByCartAndBook(eq(cart), eq(book)))
                .thenReturn(Optional.empty());

        assertThrows(BookCartNotFoundException.class,
                () -> cartService.removeItem(memberId, bookId));
    }

    @Test
    @DisplayName("특정 도서 제거 -> 성공 및 제고 복구")
    void testRemoveItem_Success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        Book book = new Book();
        BookExtraInfo extraInfo = new BookExtraInfo();
        extraInfo.setCount(5);
        book.setBookExtraInfo(extraInfo);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        BookCart bookCart = new BookCart(cart, book , 4);
        when(bookCartRepository.findByCartAndBook(cart, book)).thenReturn(Optional.of(bookCart));

        cartService.removeItem(memberId, bookId);

        verify(bookCartRepository).delete(bookCart);

        assertEquals(5, book.getBookExtraInfo().getCount());
    }

    @Test
    @DisplayName("장바구니 조회 -> 빈 변환 목록(초기)")
    void testGetCartItems_EmptyList() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId))
                .thenReturn(Optional.of(cart));

        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(Collections.emptyList());

        List<BookCart> items = cartService.getCartItems(memberId);

        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("장바구니 조회 -> 목록 조히")
    void testGetCartItems_Success() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId)).thenReturn(Optional.of(cart));

        BookCart item1 = new BookCart();
        BookCart item2 = new BookCart();

        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(Arrays.asList(item1, item2));


        List<BookCart> items = cartService.getCartItems(memberId);

        assertEquals(2, items.size());
        assertEquals(item1, items.get(0));
        assertEquals(item2, items.get(1));
    }

    @Test
    @DisplayName("카트 비우기 -> 모든 항목 삭제")
    void testCleanCart() {
        Cart cart = new Cart(memberId);
        when(cartRepository.findByMemberId(memberId))
                .thenReturn(Optional.of(cart));

        Book book1 = new Book();
        book1.setBookExtraInfo(new BookExtraInfo());
        BookCart item1 = new BookCart(cart, book1, 1);

        Book book2 = new Book();
        book2.setBookExtraInfo(new BookExtraInfo());
        BookCart item2 = new BookCart(cart, book2, 2);

        List<BookCart> items = Arrays.asList(item1, item2);
        when(bookCartRepository.findAllByCart_MemberId(memberId))
                .thenReturn(items);

        cartService.cleanCart(memberId);

        verify(bookCartRepository).deleteAll(items);
    }

}
