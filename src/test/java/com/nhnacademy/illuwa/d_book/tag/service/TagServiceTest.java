package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.repository.BookTagRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @Mock
    BookTagRepository bookTagRepository;


    @InjectMocks
    TagService tagService;

    private Tag tag;


    @BeforeEach
    void setUp() {
        tag = new Tag("태그 이름");
    }

//    @Test
//    void registerTag() {
//        TagRegisterRequest trr = new TagRegisterRequest("태그 이름");
//        String tagName = "태그 이름";
//        TagResponse tagResponse = new TagResponse(1L,"tagName");
//        given(tagRepository.existsByName(anyString())).willReturn(false);
//        given(tagRepository.save(any())).willReturn(tag);
//
//        TagResponse tagResponse1 = tagService.registerTag(trr);
//
//        Assertions.assertEquals("태그 이름", tagResponse1.getName());
//    }

    @Test
    @DisplayName("태그 삭제 - 성공")
    void deleteTag() {
        Long tagId = 11L;

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName("Test Tag");

        List<BookTag> bookTags = List.of(
                mock(BookTag.class),
                mock(BookTag.class)
        );

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        when(bookTagRepository.findByTagId(tagId)).thenReturn(bookTags);

        tagService.deleteTag(tagId);

        verify(tagRepository, times(1)).findById(tagId);
        verify(bookTagRepository, times(1)).findByTagId(tagId);
        verify(bookTagRepository, times(1)).deleteAll(bookTags);
        verify(tagRepository, times(1)).delete(tag);
    }
}