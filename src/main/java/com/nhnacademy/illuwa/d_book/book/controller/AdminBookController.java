package com.nhnacademy.illuwa.d_book.book.controller;

import com.nhnacademy.illuwa.d_book.book.dto.request.BookCountUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailWithExtraInfoResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookUpdateRequest;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.service.BookService;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
@Tag(name = "Admin Book API", description = "관리자용 도서 관련 API")
public class AdminBookController {

    private final BookService bookService;
    private final TagService tagService;


    @Operation(summary = "외부 API를 통해 도서 검색", description = "알라딘 API를 통해 도서를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookExternalResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/external")
    public ResponseEntity<List<BookExternalResponse>> searchBooksByExternalApi(@RequestParam("title") String title){
        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);
        return ResponseEntity.ok(bookExternalResponses);
    }

    // "/manual 제거 O"
    @Operation(summary = "도서 직접 등록", description = "관리자가 도서 정보를 직접 입력하여 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "도서가 성공적으로 등록되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerBookDirectly(
            @ModelAttribute BookRegisterRequest request) {

        bookService.registgerBookDirectly(request, request.getImageFile());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // "/aladin -> /external 수정 O"
    @Operation(summary = "외부 API를 통해 도서 등록", description = "외부 API에서 가져온 도서 정보를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "도서가 성공적으로 등록되었습니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/external")
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookApiRegisterRequest bookApiRegisterRequest){
        // apiDTO -> bookRegisterDTO
        BookDetailResponse detailResponse = bookService.registerBookByApi(bookApiRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
    }

    @Operation(summary = "도서 삭제", description = "도서 ID를 통해 도서를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "도서가 성공적으로 삭제되었습니다."),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서 정보 업데이트", description = "도서 ID를 통해 도서 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "도서 정보가 성공적으로 업데이트되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest requestDto
    ) {
        bookService.updateBook(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    //모든 부가정보(+카테고리, 태그)
    @Operation(summary = "도서 상세 정보 및 추가 정보 조회", description = "도서 ID를 통해 도서의 상세 정보와 추가 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 상세 정보를 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDetailWithExtraInfoResponse.class))),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}/detail")
    public ResponseEntity<BookDetailWithExtraInfoResponse> getBookDetailWithExtra(@PathVariable Long id) {
        BookDetailWithExtraInfoResponse response = bookService.getBookDetailWithExtraInfo(id);
        return ResponseEntity.ok(response);
    }



    // extra_info -> details 수정
    @Operation(summary = "도서 및 추가 정보 목록 조회", description = "모든 도서의 상세 정보와 추가 정보를 페이징하여 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 도서 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BookDetailWithExtraInfoResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/details")
    public ResponseEntity<Page<BookDetailWithExtraInfoResponse>> getBooksWithExtraInfo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC; // 기본값

        if (sortParams.length > 1) {
            direction = Sort.Direction.fromString(sortParams[1]);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<BookDetailWithExtraInfoResponse> response = bookService.getAllBooksWithExtraInfo(pageable);

        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "도서 재고 수량 변경",
            description = "도서의 수량을 변경합니다. 'sign' 헤더가 positive이면 증가, negative이면 감소입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공적으로 처리됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/update/bookCount")
    public ResponseEntity<Void> updateBooksCount(
            @RequestBody List<BookCountUpdateRequest> requests,
            @RequestHeader("sign") String sign
    ) {
        bookService.updateBooksCount(requests, sign);
        return ResponseEntity.noContent().build();
    }


//    @Operation(summary = "도서 수량 복원", description = "주문 취소 시 도서의 재고 수량을 복원합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "204", description = "도서 수량이 성공적으로 복원되었습니다."),
//        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
//        @ApiResponse(responseCode = "500", description = "서버 오류")
//    })
//    @PutMapping("/restore/bookCount")
//    public ResponseEntity<Void> restoreBooksCount(
//            @RequestBody List<BookCountUpdateRequest> requests
//    ) {
//        bookService.restoreBooksCount(requests);
//        return ResponseEntity.noContent().build();
//    }

    @Operation(summary = "도서에 태그 추가", description = "특정 도서에 태그를 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태그가 성공적으로 추가되었습니다."),
        @ApiResponse(responseCode = "404", description = "도서 또는 태그를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToBook(@PathVariable Long bookId, @PathVariable Long tagId) {
        tagService.addTagToBook(bookId, tagId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "도서에서 태그 제거", description = "특정 도서에서 태그를 제거합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "태그가 성공적으로 제거되었습니다."),
        @ApiResponse(responseCode = "404", description = "도서 또는 태그를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromBook(@PathVariable Long bookId, @PathVariable Long tagId) {
        tagService.removeTagFromBook(bookId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서에 연결된 태그 조회", description = "특정 도서에 연결된 모든 태그를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 태그 목록을 반환합니다.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TagResponse.class))),
        @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{bookId}/tags")
    public ResponseEntity<List<TagResponse>> getTagsByBookId(@PathVariable Long bookId) {
        List<TagResponse> tags = tagService.getTagsByBookId(bookId);
        return ResponseEntity.ok(tags);
    }




//    부가 정보 포함 (임시 주석 처리
//    @GetMapping("/{id}/details")
//    public ResponseEntity<BookDetailResponse> getBookDetail(@PathVariable Long id) {
//        BookDetailResponse response = bookService.searchBookById(id);
//        return ResponseEntity.ok(response);
//    }


}
