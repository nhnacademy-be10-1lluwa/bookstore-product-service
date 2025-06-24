package com.nhnacademy.illuwa.d_book.book.extrainfo;

import com.nhnacademy.illuwa.d_book.book.enums.Status;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookExtraInfo {

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean giftwrap;

    private Integer count;

}
