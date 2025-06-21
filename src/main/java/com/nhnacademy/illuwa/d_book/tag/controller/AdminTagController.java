package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/admin/tags")
public class AdminTagController {

    TagService tagService;

    AdminTagController(TagService tagService){
        this.tagService = tagService;
    }

    @PostMapping()
    public ResponseEntity<TagResponse> registerTag(@RequestBody @Valid TagRegisterRequest tagRegisterRequest){
        Tag registeredTag = tagService.registerTag(tagRegisterRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registeredTag.getId())
                .toUri();

        TagResponse tagResponse = new TagResponse(registeredTag.getId(),registeredTag.getName());

        return ResponseEntity.created(location).body(tagResponse);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id){
        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}
