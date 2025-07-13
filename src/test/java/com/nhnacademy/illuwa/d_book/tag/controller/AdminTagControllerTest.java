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
import static org.mockito.BDDMockito.given;
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
        Tag registerTag = new Tag("태그이름1");

        TagRegisterRequest tagRegisterRequest = new TagRegisterRequest("태그 이름");
        TagResponse tagResponse = new TagResponse(11L,"태그 이름");


        //메소드 호출의 인자(registerTag)까지 정확히 일치하는지 확인
        //내용은 같지만 메모리 주소가 다른 별개의 객체
        given(tagService.registerTag(any(TagRegisterRequest.class))).willReturn(tagResponse);

        String json = objectMapper.writeValueAsString(tagRegisterRequest);


        //when & then
        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(jsonPath("$.name").value("태그 이름"))
                .andExpect(status().isOk());

    }

    @Test
    void deleteTag() throws Exception {
        Long id = 1L;

        //when & then
        mockMvc.perform(delete("/api/tags/{id}", 11L))
                .andExpect(status().isNoContent());

    }
}