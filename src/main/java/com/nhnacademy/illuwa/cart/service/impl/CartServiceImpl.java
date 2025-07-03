package com.nhnacademy.illuwa.cart.service.impl;

import com.nhnacademy.illuwa.cart.dto.BookCartRequest;
import com.nhnacademy.illuwa.cart.dto.BookCartResponse;
import com.nhnacademy.illuwa.cart.dto.CartRequest;
import com.nhnacademy.illuwa.cart.dto.CartResponse;
import com.nhnacademy.illuwa.cart.entity.BookCart;
import com.nhnacademy.illuwa.cart.entity.Cart;
import com.nhnacademy.illuwa.cart.exception.BookCartNotFoundException;
import com.nhnacademy.illuwa.cart.exception.BookDiscontinuedException;
import com.nhnacademy.illuwa.cart.exception.InsufficientStockException;
import com.nhnacademy.illuwa.cart.repository.BookCartRepository;
import com.nhnacademy.illuwa.cart.repository.CartRepository;
import com.nhnacademy.illuwa.cart.service.CartService;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.Status;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookCartRepository bookCartRepository;
    private final BookRepository bookRepository;


//    @Override
//    public Cart getOrCreateCart(Long memberId) {
//
//        return cartRepository.findByMemberId(memberId)
//                .orElseGet(() -> cartRepository.save(new Cart(memberId)));
//    }
//
//    @Override
//    public BookCart addItem(Long memberId, Long bookId, int amount) {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책은 존재하지 않습니다."));
//
//        Cart cart = getOrCreateCart(memberId);
//
//        BookExtraInfo ext = book.getBookExtraInfo();
//        int stock = (ext != null && ext.getCount() != null)
//                ? ext.getCount() : 0;
//
//        Optional<BookCart> existing = bookCartRepository.findByCartAndBook(cart, book);
//        int existingAmount = existing.map(BookCart::getAmount).orElse(0);
//
//        if (stock < amount + existingAmount) {
//            throw new InsufficientStockException("제품의 수량이 부족합니다.");
//        }
//
//        if(existing.isPresent()) {
//            BookCart bookCart = existing.get();
//            bookCart.setAmount(bookCart.getAmount() + amount);
//            bookCartRepository.save(bookCart);
//            return bookCart;
//        }
//
//        return bookCartRepository.save(new BookCart(cart, book, amount));
//    }
//
//    @Override
//    public BookCart updateItem(Long memberId, Long bookId, int amount) {
//
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책을 발견하지 못했습니다."));
//
//        BookExtraInfo ext = book.getBookExtraInfo();
//        int stock = (ext != null && ext.getCount() != null)
//                ? ext.getCount() : 0;
//
//        if (amount > stock) {
//            throw new InsufficientStockException("재품의 수량이 부족합니다.");
//        }
//
//        Cart cart = getOrCreateCart(memberId);
//
//        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
//                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 삼품이 아닙니다."));
//
//        if (amount == 0) {
//            bookCart.setAmount(0);
//            bookCartRepository.delete(bookCart);
//        } else {
//            bookCart.setAmount(amount);
//        }
//        return bookCart;
//    }
//
//    @Override
//    public void removeItem(Long memberId, Long bookId) {
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책이 존재하지 않습니다."));
//
//        Cart cart = getOrCreateCart(memberId);
//
//        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
//                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 상품이 아닙니다."));
//
//        bookCartRepository.delete(bookCart);
//    }
//
//    @Override
//    public List<BookCart> getCartItems(Long memberId) {
//        getOrCreateCart(memberId);
//
//        return bookCartRepository.findAllByCart_MemberId(memberId);
//    }
//
//    @Override
//    public void cleanCart(Long memberId) {
//        Cart cart = getOrCreateCart(memberId);
//
//        List<BookCart> bookCarts = bookCartRepository.findAllByCart_MemberId(memberId);
//
//        bookCartRepository.deleteAll(bookCarts);
//    }


    @Override
    public Cart getOrCreateCart(CartRequest request) {

        return cartRepository.findByMemberId(request.getMemberId())
                .orElseGet(() -> cartRepository.save(new Cart(request.getMemberId())));
    }

    @Override
    public CartResponse getCart(CartRequest request) {
        Cart cart = getOrCreateCart(request);
        List<BookCart> bookCarts = bookCartRepository.findAllByCart_MemberId(request.getMemberId());

        List<BookCartResponse> books = bookCarts.stream()
                .map(bookCart -> new BookCartResponse(
                        bookCart.getBook().getId(),
                        bookCart.getBook().getTitle(),
                        bookCart.getAmount()
                )).toList();
        return new CartResponse(cart.getCartId(), books);
    }

    @Override
    public BookCartResponse addBook(BookCartRequest request) {
        // 책이 존재 여부
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책은 존재하지 않습니다."));

        if (book.getBookExtraInfo().getStatus() == Status.DISCONTINUED) {
            throw new BookDiscontinuedException("이 도서는 더 이상 판매하지 않는 상품입니다.");
        }

        Cart cart = getOrCreateCart(new CartRequest(request.getMemberId()));

        // 수량 정검
        BookExtraInfo ext = book.getBookExtraInfo();
        int stock = (ext != null && ext.getCount() != null) ?  ext.getCount() : 0;

        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
                .orElseGet(() -> new BookCart(cart, book, 0));

        int totalAmount = bookCart.getAmount() + request.getAmount();

        // 구매 수량이 제품 제고보다 많을 경우
        if (stock < totalAmount) {
            throw new InsufficientStockException("재품의 수량이 부족합니다.");
        }

        // 적다면 수량을 추가 후 저장
        bookCart.setAmount(totalAmount);
        bookCartRepository.save(bookCart);

        return new BookCartResponse(book.getId(), book.getTitle(), bookCart.getAmount());
    }

    @Override
    public BookCartResponse updateBookCart(BookCartRequest request) {

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책은 존재하지 않습니다."));

        Cart cart = getOrCreateCart(new CartRequest(request.getMemberId()));

        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 상품이 아닙니다."));

        BookExtraInfo ext = book.getBookExtraInfo();
        // 제고 관리
        // 기존 추가한 양을 합친양보다 작을 경우 오류 발생 해야함
        int stock = (ext != null && ext.getCount() != null) ?  ext.getCount() : 0;

        if (request.getAmount() == 0) {
            bookCartRepository.delete(bookCart);
            return new BookCartResponse(book.getId(), book.getTitle(), 0);
        }

        bookCart.setAmount(request.getAmount());
        bookCartRepository.save(bookCart);

        return new BookCartResponse(book.getId(), book.getTitle(), bookCart.getAmount());
    }

    @Override
    public void removeBookCart(BookCartRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책은 존재하지 않습니다."));

        Cart cart = getOrCreateCart(new CartRequest(request.getMemberId()));

        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 상품이 아닙니다."));

        bookCartRepository.delete(bookCart);
    }

    @Override
    public void cleanCart(CartRequest request) {
        List<BookCart> bookCarts = bookCartRepository.findAllByCart_MemberId(request.getMemberId());
        bookCartRepository.deleteAll(bookCarts);
    }
}