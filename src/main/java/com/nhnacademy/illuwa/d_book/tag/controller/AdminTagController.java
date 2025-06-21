package com.nhnacademy.illuwa.d_book.tag.controller;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/admin/tags")
public class AdminTagController {

    TagService tagService;

    AdminTagController(TagService tagService){
        this.tagService = tagService;
    }

    // TODO : 1) POST
    @PostMapping()
    public ResponseEntity<Map<String,String>> registerTag(@RequestBody TagRegisterRequest tagRegisterRequest){
        tagService.registerTag(tagRegisterRequest);
        Map<String,String> response = new HashMap<>();
        response.put("tagName",tagRegisterRequest.getName());
        return ResponseEntity.ok(response);
    }

    // TODO : 2) DELETE
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId){
        tagService.deleteTag(tagId);

        return ResponseEntity.ok().build();
    }
}
