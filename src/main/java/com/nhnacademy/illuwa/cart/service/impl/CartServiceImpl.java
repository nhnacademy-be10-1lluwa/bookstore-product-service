package com.nhnacademy.illuwa.cart.service.impl;

import com.nhnacademy.illuwa.cart.entity.BookCart;
import com.nhnacademy.illuwa.cart.entity.Cart;
import com.nhnacademy.illuwa.cart.exception.BookCartNotFoundException;
import com.nhnacademy.illuwa.cart.exception.InsufficientStockException;
import com.nhnacademy.illuwa.cart.repository.BookCartRepository;
import com.nhnacademy.illuwa.cart.repository.CartRepository;
import com.nhnacademy.illuwa.cart.service.CartService;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import com.nhnacademy.illuwa.d_book.book.extrainfo.BookExtraInfo;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookCartRepository bookCartRepository;
    private final BookRepository bookRepository;

    @Override
    public Cart getOrCreateCart(Long memberId) {

        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));
    }

    @Override
    public BookCart addItem(Long memberId, Long bookId, int amount) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책은 존재하지 않습니다."));

        Cart cart = getOrCreateCart(memberId);

        BookExtraInfo ext = book.getBookExtraInfo();
        int stock = (ext != null && ext.getCount() != null)
                ? ext.getCount() : 0;

        Optional<BookCart> existing = bookCartRepository.findByCartAndBook(cart, book);
        int existingAmount = existing.map(BookCart::getAmount).orElse(0);

        if (stock < amount + existingAmount) {
            throw new InsufficientStockException("제품의 수량이 부족합니다.");
        }

        if(existing.isPresent()) {
            BookCart bookCart = existing.get();
            bookCart.setAmount(bookCart.getAmount() + amount);
            bookCartRepository.save(bookCart);
            return bookCart;
        }

        return bookCartRepository.save(new BookCart(cart, book, amount));
    }

    @Override
    public BookCart updateItem(Long memberId, Long bookId, int amount) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책을 발견하지 못했습니다."));

        BookExtraInfo ext = book.getBookExtraInfo();
        int stock = (ext != null && ext.getCount() != null)
                ? ext.getCount() : 0;

        if (amount > stock) {
            throw new InsufficientStockException("재품의 수량이 부족합니다.");
        }

        Cart cart = getOrCreateCart(memberId);

        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 삼품이 아닙니다."));

        if (amount == 0) {
            bookCart.setAmount(0);
            bookCartRepository.delete(bookCart);
        } else {
            bookCart.setAmount(amount);
        }
        return bookCart;
    }

    @Override
    public void removeItem(Long memberId, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundBookException("해당 ID에 적합한 책이 존재하지 않습니다."));

        Cart cart = getOrCreateCart(memberId);

        BookCart bookCart = bookCartRepository.findByCartAndBook(cart, book)
                .orElseThrow(() -> new BookCartNotFoundException("장바구니에 담긴 상품이 아닙니다."));

        bookCartRepository.delete(bookCart);
    }

    @Override
    public List<BookCart> getCartItems(Long memberId) {
        getOrCreateCart(memberId);

        return bookCartRepository.findAllByCart_MemberId(memberId);
    }

    @Override
    public void cleanCart(Long memberId) {
        Cart cart = getOrCreateCart(memberId);

        List<BookCart> bookCarts = bookCartRepository.findAllByCart_MemberId(memberId);

        bookCartRepository.deleteAll(bookCarts);
    }
}
