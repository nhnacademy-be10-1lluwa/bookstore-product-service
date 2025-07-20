package com.nhnacademy.illuwa.d_book.book.service;


import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookLikeService {
    boolean isLikedByMe(Long bookId, Long memberId);

    void toggleBookLikes(Long bookId, Long memberId);

    Page<BestSellerResponse> getLikedBooksByMember(Long memberId, Pageable pageable);
}
