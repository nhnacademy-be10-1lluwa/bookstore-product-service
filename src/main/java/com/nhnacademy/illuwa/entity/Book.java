package com.nhnacademy.illuwa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
public class Book {

    @Id
    private Long id;

    @Column(length = 255)
    private String Bookname;

    private String Contents;

    private String Description;

    @Column(length = 255)
    private String Author;

    @Column(length = 255)
    private String Publisher;

    private Date published_datetime;

    @Column(length = 17)
    private String ISBN;

    private int regular_price;

    private int sale_price;

    private boolean isGiftWrap;

//    카테고리 Entity 추가 후 필드 생성
}