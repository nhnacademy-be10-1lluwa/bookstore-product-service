package com.nhnacademy.illuwa.d_book.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.d_book.tag.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
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