package com.nhnacademy.illuwa.d_book.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.common.dto.PageResponse;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTagController.class)
class AdminTagControllerTest {



    @Autowired
    MockMvc mockMvc;

    @MockBean
    TagService tagService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private TagRepository tagRepository;


    @Test
    @DisplayName("태그 목록 조회 - 성공")
    void getTags_Success() throws Exception {
        // Given
        TagResponse tagResponse = new TagResponse(1L, "테스트 태그");
        Page<TagResponse> page = new PageImpl<>(Collections.singletonList(tagResponse));
        given(tagService.getAllTags(any(Pageable.class))).willReturn(page);

        // When & Then
        mockMvc.perform(get("/api/admin/tags")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("테스트 태그"));

        verify(tagService, times(1)).getAllTags(any(Pageable.class));
    }

    @Test
    void registerTag() throws Exception {
        TagRegisterRequest tagRegisterRequest = new TagRegisterRequest("태그 이름");

        TagResponse tagResponse = new TagResponse(11L, "태그 이름");

        given(tagService.registerTag(anyString())).willReturn(tagResponse);

        String json = objectMapper.writeValueAsString(tagRegisterRequest);

        mockMvc.perform(post("/api/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("태그 이름"));
    }

    @Test
    void deleteTag() throws Exception {
        Long id = 1L;

        //when & then
        mockMvc.perform(delete("/api/admin/tags/{id}", 11L))
                .andExpect(status().isNoContent());

    }
}