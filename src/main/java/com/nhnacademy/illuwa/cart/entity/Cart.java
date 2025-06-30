package com.nhnacademy.illuwa.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "cart")
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @Setter
    @Column(name = "member_id")
    private Long memberId;

    public Cart(Long memberId) {
        this.memberId = memberId;
    }
}
