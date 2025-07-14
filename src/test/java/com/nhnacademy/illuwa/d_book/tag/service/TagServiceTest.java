package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagService tagService;

    private Tag tag;


    @BeforeEach
    void setUp() {
        tag = new Tag("태그 이름");
    }

    @Test
    void registerTag() {
        TagRegisterRequest trr = new TagRegisterRequest("태그 이름");
        String tagName = "태그 이름";
        TagResponse tagResponse = new TagResponse(1L,"tagName");
        given(tagRepository.existsByName(anyString())).willReturn(false);
        given(tagRepository.save(any())).willReturn(tag);

        TagResponse tagResponse1 = tagService.registerTag(trr);

        Assertions.assertEquals("태그 이름", tagResponse1.getName());

    }

    @Test
    void deleteTag() {

        Long tagId = 11L;

        tagService.deleteTag(tagId);

        verify(tagRepository).deleteById(tagId);

    }
}