package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/tags")
public class TagController {

    TagService tagService;

    TagRepository tagRepository;

    TagController(TagService tagService, TagRepository tagRepository){
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    @PostMapping()
    public ResponseEntity<TagResponse> registerTag(@RequestBody @Valid TagRegisterRequest tagRegisterRequest){
        return ResponseEntity.ok(tagService.registerTag(tagRegisterRequest));
    }

    @GetMapping()
    public ResponseEntity<Page<TagResponse>> getAllTags(Pageable pageable){
        Page<TagResponse> allTags = tagService.getAllTags(pageable).map(t->new TagResponse(t.getId(),t.getName()));
        return ResponseEntity.ok(allTags);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
