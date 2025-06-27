package com.nhnacademy.illuwa.cart.entity;


import com.nhnacademy.illuwa.d_book.book.entity.Book;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "book_cart")
public class BookCart {

    @Id
    @Column(name = "bookcart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookCartId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Setter
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Setter
    @Column(name = "amount", nullable = false)
    private int amount;

    public BookCart(Cart cart, Book book, int amount) {
        this.book = book;
        this.cart = cart;
        this.amount = amount;
    }

}
