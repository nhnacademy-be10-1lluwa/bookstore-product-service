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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.List;

@Tag(name = "관리자 도서 API", description = "관리자를 위한 도서 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final TagService tagService;


    @Operation(summary = "외부 API로 도서 검색", description = "알라딘 API를 통해 도서를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "도서 검색 성공",
                    content = @Content(schema = @Schema(implementation = BookExternalResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/external")
    public ResponseEntity<List<BookExternalResponse>> searchBooksByExternalApi(@Parameter(description = "검색할 도서 제목", required = true) @RequestParam("title") String title){
        List<BookExternalResponse> bookExternalResponses = bookService.searchBookFromExternalApi(title);
        return ResponseEntity.ok(bookExternalResponses);
    }

    @Operation(summary = "도서 직접 등록", description = "관리자가 도서 정보를 직접 입력하여 등록합니다. 이미지 파일 포함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 도서",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerBookDirectly(
            @ModelAttribute BookRegisterRequest request) {

        bookService.registgerBookDirectly(request, request.getImageFile());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "외부 API 데이터로 도서 등록", description = "외부 API에서 가져온 데이터를 기반으로 도서를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "도서 등록 성공",
                    content = @Content(schema = @Schema(implementation = BookDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 도서",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/external")
    public ResponseEntity<BookDetailResponse> registerBook(@RequestBody @Valid BookApiRegisterRequest bookApiRegisterRequest){
        BookDetailResponse detailResponse = bookService.registerBookByApi(bookApiRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
    }

    @Operation(summary = "도서 삭제", description = "도서 ID를 통해 도서를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "도서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@Parameter(description = "삭제할 도서 ID", required = true) @PathVariable Long id){
        bookService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서 정보 수정", description = "도서 ID를 통해 도서 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "도서 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateBook(
            @Parameter(description = "수정할 도서 ID", required = true) @PathVariable Long id,
            @RequestBody BookUpdateRequest requestDto
    ) {
        bookService.updateBook(id, requestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서 상세 정보 및 부가 정보 조회", description = "도서 ID를 통해 도서의 상세 정보와 부가 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BookDetailWithExtraInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{id}/detail")
    public ResponseEntity<BookDetailWithExtraInfoResponse> getBookDetailWithExtra(@Parameter(description = "조회할 도서 ID", required = true) @PathVariable Long id) {
        BookDetailWithExtraInfoResponse response = bookService.getBookDetailWithExtraInfo(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 목록 상세 조회 (페이징)", description = "모든 도서의 상세 정보와 부가 정보를 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/details")
    public ResponseEntity<Page<BookDetailWithExtraInfoResponse>> getBooksWithExtraInfo(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 (예: id,desc)", example = "id,desc") @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParams.length > 1) {
            direction = Sort.Direction.fromString(sortParams[1]);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        Page<BookDetailWithExtraInfoResponse> response = bookService.getAllBooksWithExtraInfo(pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 수량 차감", description = "주문 발생 시 도서의 재고 수량을 차감합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "수량 차감 성공"),
            @ApiResponse(responseCode = "400", description = "재고 부족 또는 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/update/bookCount")
    public ResponseEntity<Void> deductBooksCount(
            @RequestBody List<BookCountUpdateRequest> requests
    ) {
        bookService.updateBooksCount(requests);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서 수량 복구", description = "주문 취소 시 도서의 재고 수량을 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "수량 복구 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/restore/bookCount")
    public ResponseEntity<Void> restoreBooksCount(
            @RequestBody List<BookCountUpdateRequest> requests
    ) {
        bookService.restoreBooksCount(requests);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서에 태그 추가", description = "특정 도서에 태그를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 추가 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 태그를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> addTagToBook(@Parameter(description = "도서 ID", required = true) @PathVariable Long bookId, @Parameter(description = "태그 ID", required = true) @PathVariable Long tagId) {
        tagService.addTagToBook(bookId, tagId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "도서에서 태그 삭제", description = "특정 도서에서 태그를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "태그 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "도서 또는 태그를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping("/{bookId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromBook(@Parameter(description = "도서 ID", required = true) @PathVariable Long bookId, @Parameter(description = "태그 ID", required = true) @PathVariable Long tagId) {
        tagService.removeTagFromBook(bookId, tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "도서의 태그 목록 조회", description = "특정 도서에 연결된 모든 태그를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "404", description = "도서를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{bookId}/tags")
    public ResponseEntity<List<TagResponse>> getTagsByBookId(@Parameter(description = "도서 ID", required = true) @PathVariable Long bookId) {
        List<TagResponse> tags = tagService.getTagsByBookId(bookId);
        return ResponseEntity.ok(tags);
    }
}
