package com.nhnacademy.illuwa.d_book.book.extrainfo;

import com.nhnacademy.illuwa.d_book.book.enums.Status;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookExtraInfo {

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean giftwrap;

}
