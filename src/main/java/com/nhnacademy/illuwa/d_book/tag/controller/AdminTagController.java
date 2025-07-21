package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.common.dto.PageResponse;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/admin")
public class AdminTagController {

    private final TagService tagService;


    AdminTagController(TagService tagService){
        this.tagService = tagService;
    }


    @GetMapping("/tags")
    public ResponseEntity<PageResponse<TagResponse>> getTags(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<TagResponse> result = tagService.getAllTags(pageable);

        PageResponse<TagResponse> response = new PageResponse<>(
                result.getContent(),
                result.getTotalPages(),
                result.getTotalElements(),
                result.isLast(),
                result.getSize(),
                result.getNumber()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponse> createTag(@RequestBody TagRegisterRequest request) {
        TagResponse tag = tagService.registerTag(request.getName());
        return ResponseEntity.ok(tag);
    }



    @DeleteMapping("tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
