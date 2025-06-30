package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/admin/tags")
public class AdminTagController {

    TagService tagService;

    TagRepository tagRepository;

    AdminTagController(TagService tagService,TagRepository tagRepository){
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    @PostMapping()
    public ResponseEntity<TagResponse> registerTag(@RequestBody @Valid TagRegisterRequest tagRegisterRequest){



        return ResponseEntity.ok(tagService.registerTag(tagRegisterRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
