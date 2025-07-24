package com.nhnacademy.illuwa.d_review.review.controller;

import com.nhnacademy.illuwa.d_review.review.dto.ReviewRequest;
import com.nhnacademy.illuwa.d_review.review.dto.ReviewResponse;
import com.nhnacademy.illuwa.d_review.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "리뷰를 작성합니다.")
    @PostMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> createReview(@RequestParam(name = "book-id") long bookId,
                                                       @RequestHeader("X-USER-ID") long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.createReview(bookId, memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "리뷰 목록 가져오기 (페이징)", description = "패이징 처리된 리뷰 목록을 가져옵니다.")
    @GetMapping(value = "/api/public/reviews")
    public ResponseEntity<Page<ReviewResponse>> getReviewPages(@RequestParam(name = "book-id") long bookId,
                                                               @PageableDefault(size = 5, sort = "reviewDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewResponse> responsePage = reviewService.getReviewPages(bookId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "작성 리뷰목록 가져오기 (페이징)", description = "현재 접속한 사용자가 쓴 리뷰 목록을 가져옵니다.")
    @GetMapping(value = "/api/reviews")
    public ResponseEntity<Page<ReviewResponse>> getMemberReviewPages(@RequestHeader("X-USER-ID") long memberId,
                                                                     @RequestParam("page") int page, @RequestParam("size") int size){

        Pageable pageable = PageRequest.of(page, size, Sort.by("reviewDate").descending());
        Page<ReviewResponse> responsePage = reviewService.getMemberReviewPages(memberId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(summary = "리뷰 상세 가져오기", description = "리뷰 단일 객체의 정보를 불러옵니다.")
    @GetMapping(value = "/api/reviews/{review-id}")
    public ResponseEntity<ReviewResponse> getReviewDetails(@RequestParam(name = "book-id") long bookId,
                                                           @PathVariable(name = "review-id") long reviewId,
                                                           @RequestHeader("X-USER-ID") long memberId) {

        ReviewResponse response = reviewService.getReviewDetails(bookId, reviewId, memberId);
        return ResponseEntity.ok(response);
    }

    // 프론트에서 feign 으로 수정요청 받으려면 어쩔수 없이 Post 써야함 (feign 은 patch 미지원)
    @Operation(summary = "리뷰 수정하기", description = "리뷰를 수정합니다.")
    @PostMapping(value = "/api/reviews/{review-id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> updateReview(@RequestParam(name = "book-id") long bookId,
                                                       @PathVariable(name = "review-id") long reviewId,
                                                       @RequestHeader("X-USER-ID") Long memberId,
                                                       @ModelAttribute @Valid ReviewRequest request) throws Exception {

        ReviewResponse response = reviewService.updateReview(bookId, reviewId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 ID 가져오기", description = "도서 목록이 가지고있는 각각의 리뷰들을 맵으로 가져옵니다.")
    @PostMapping("/api/reviews/check")
    public ResponseEntity<Map<Long, Long>> getExistingReviewIdMap(@RequestBody List<Long> bookIds,
                                                                  @RequestHeader("X-USER-ID") Long memberId){

        return ResponseEntity.ok(reviewService.getExistingReviewIdMap(bookIds, memberId));
    }

    @Operation(summary = "책 제목 가져오기", description = "리뷰 id 목록을 기준으로 책의 제목을 맵으로 가져옵니다.")
    @GetMapping("/api/reviews/book-title")
    public ResponseEntity<Map<Long, String>> getBookTitleMapFromReviewIds(@RequestParam("review-ids") Collection<Long> reviewIds) {

        return ResponseEntity.ok(reviewService.getBookTitleMapFromReviewIds(reviewIds));
    }
}