package com.nhnacademy.illuwa.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCartResponse {
    // memberId 같은 경우는 api 명세에 나타남
    // 추가사항 -> 금액
    private Long bookId;
    private String title;
    private int amount;
}
