package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.tag.exception.TagNotFoundException;
import com.nhnacademy.illuwa.d_book.tag.repository.BookTagRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import com.nhnacademy.illuwa.search.service.BookSearchService;
import com.nhnacademy.illuwa.search.service.BookSearchService;
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

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.exception.NotFoundBookException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    BookRepository bookRepository;

    @Mock
    BookSearchService bookSearchService;



    @InjectMocks
    TagService tagService;

    private Tag tag;


    @BeforeEach
    void setUp() {
        tag = new Tag("태그 이름");
    }

    @Test
    @DisplayName("태그 등록 - 성공")
    void registerTag_Success() {
        // Given
        String tagName = "새 태그";
        Tag newTag = new Tag(tagName);
        newTag.setId(1L);

        given(tagRepository.existsByName(tagName)).willReturn(false);
        given(tagRepository.save(any(Tag.class))).willReturn(newTag);

        // When
        TagResponse result = tagService.registerTag(tagName);

        // Then
        Assertions.assertEquals(tagName, result.getName());
        verify(tagRepository, times(1)).existsByName(tagName);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("태그 등록 - 실패 (이미 존재하는 태그)")
    void registerTag_AlreadyExists_Failure() {
        // Given
        String tagName = "기존 태그";
        given(tagRepository.existsByName(tagName)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> tagService.registerTag(tagName))
                .isInstanceOf(TagAlreadyExistsException.class)
                .hasMessageContaining("이미 존재하는 태그입니다.");

        verify(tagRepository, times(1)).existsByName(tagName);
        verify(tagRepository, never()).save(any(Tag.class));
    }

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

    @Test
    @DisplayName("태그 삭제 - 실패 (태그 없음)")
    void deleteTag_NotFound() {
        // Given
        Long tagId = 1L;
        given(tagRepository.findById(tagId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.deleteTag(tagId))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("존재하지 않는 태그입니다.");

        verify(tagRepository, times(1)).findById(tagId);
        verify(bookTagRepository, never()).findByTagId(anyLong());
        verify(bookTagRepository, never()).deleteAll(anyList());
        verify(tagRepository, never()).delete(any(Tag.class));
    }

    @Test
    @DisplayName("도서에 태그 추가 - 성공")
    void addTagToBook_Success() {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;
        Tag mockTag = mock(Tag.class);
        Book mockBook = mock(Book.class);

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(mockTag));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        when(bookTagRepository.existsByBookIdAndTagId(bookId, tagId)).thenReturn(false);

        // When
        tagService.addTagToBook(bookId, tagId);

        // Then
        verify(tagRepository, times(1)).findById(tagId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookTagRepository, times(1)).existsByBookIdAndTagId(bookId, tagId);
        verify(bookTagRepository, times(1)).save(any(BookTag.class));
    }
//
//    @Test
//    @DisplayName("도서에 태그 추가 - 실패 (태그 없음)")
//    void addTagToBook_TagNotFound_Failure() {
//        // Given
//        Long bookId = 1L;
//        Long tagId = 1L;
//
//        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> tagService.addTagToBook(bookId, tagId))
//                .isInstanceOf(TagNotFoundException.class)
//                .hasMessageContaining("해당 아이디의 태그를 찾을 수 없습니다. " + tagId);
//
//        verify(tagRepository, times(1)).findById(tagId);
//        verify(bookTagRepository, never()).existsByBookIdAndTagId(anyLong(), anyLong());
//        verify(bookTagRepository, never()).save(any(BookTag.class));
//    }

//    @Test
//    @DisplayName("도서에 태그 추가 - 실패 (도서 없음)")
//    void addTagToBook_BookNotFound_Failure() {
//        // Given
//        Long bookId = 1L;
//        Long tagId = 1L;
//
//        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> tagService.addTagToBook(bookId, tagId))
//                .isInstanceOf(NotFoundBookException.class)
//                .hasMessageContaining("해당 아이디의 도서를 찾을 수 없습니다.: " + bookId);
//
//        verify(bookRepository, times(1)).findById(bookId);
//
//        verify(bookTagRepository, never()).existsByBookIdAndTagId(anyLong(), anyLong());
//        verify(bookTagRepository, never()).save(any(BookTag.class));
//        verify(tagRepository, never()).findById(anyLong());
//    }

    @Test
    @DisplayName("도서에 태그 추가 - 실패 (이미 추가된 태그)")
    void addTagToBook_AlreadyAdded_Failure() {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;

        when(bookTagRepository.existsByBookIdAndTagId(bookId, tagId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> tagService.addTagToBook(bookId, tagId))
                .isInstanceOf(TagAlreadyExistsException.class)
                .hasMessageContaining("이미 존재하는 태그입니다.");

        verify(bookTagRepository, times(1)).existsByBookIdAndTagId(bookId, tagId);
        verify(bookRepository, never()).findById(anyLong());
        verify(bookTagRepository, never()).save(any(BookTag.class));
    }

    @Test
    @DisplayName("도서에서 태그 제거 - 성공")
    void removeTagFromBook_Success() {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(mock(Tag.class)));

        // When
        tagService.removeTagFromBook(bookId, tagId);

        // Then
        verify(tagRepository, times(1)).findById(tagId);
        verify(bookTagRepository, times(1)).deleteByBookIdAndTagId(bookId, tagId);
    }

    @Test
    @DisplayName("도서에서 태그 제거 - 실패 (BookTag 없음)")
    void removeTagFromBook_NotFound_Failure() {
        // Given
        Long bookId = 1L;
        Long tagId = 1L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> tagService.removeTagFromBook(bookId, tagId))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessageContaining("해당 아이디의 태그를 찾을 수 없습니다. " + tagId);

        verify(tagRepository, times(1)).findById(tagId);
        verify(bookTagRepository, never()).deleteByBookIdAndTagId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("도서에 연결된 태그 조회 - 성공")
    void getTagsByBookId_Success() {
        // Given
        Long bookId = 1L;
        Tag tag1 = new Tag("태그1");
        tag1.setId(1L);
        Tag tag2 = new Tag("태그2");
        tag2.setId(2L);

        BookTag bookTag1 = BookTag.builder().tag(tag1).build();
        BookTag bookTag2 = BookTag.builder().tag(tag2).build();

        List<BookTag> bookTags = List.of(bookTag1, bookTag2);

        when(bookTagRepository.findByBookId(bookId)).thenReturn(bookTags);

        // When
        List<TagResponse> result = tagService.getTagsByBookId(bookId);

        // Then
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("태그1", result.get(0).getName());
        Assertions.assertEquals("태그2", result.get(1).getName());
        verify(bookTagRepository, times(1)).findByBookId(bookId);
    }
}