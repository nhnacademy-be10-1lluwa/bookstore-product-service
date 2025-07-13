package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.common.dto.PageResponse;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/admin")
public class AdminTagController {

    TagService tagService;

    TagRepository tagRepository;

    AdminTagController(TagService tagService, TagRepository tagRepository){
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }


    @GetMapping("/tags")
    public ResponseEntity<PageResponse<TagResponse>> getTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));

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
    public ResponseEntity<TagResponse> createTag(@RequestBody TagResponse request) {
        TagResponse tag = tagService.registerTag(request.getName());
        return ResponseEntity.ok(tag);
    }



    @DeleteMapping("tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
