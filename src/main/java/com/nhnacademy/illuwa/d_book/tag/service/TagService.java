package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.repository.BookRepository;
import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.BookTag;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.tag.repository.BookTagRepository;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TagService {

    private final TagRepository tagRepository;
    private final BookRepository bookRepository;
    private final BookTagRepository bookTagRepository;

    public TagService(TagRepository tagRepository, BookRepository bookRepository, BookTagRepository bookTagRepository ){
        this.tagRepository = tagRepository;
        this.bookRepository = bookRepository;
        this.bookTagRepository = bookTagRepository;
    }

    @Transactional
    public TagResponse registerTag(TagRegisterRequest tagRegisterRequest) {

        String tagName = tagRegisterRequest.getName();

        if(tagRepository.existsByName(tagName)){
            throw new TagAlreadyExistsException("이미 존재하는 태그입니다.");
        }

        //request -> entity 변환 필요없음, 태그명만 있으면 entity 생성가능
        Tag tag = new Tag(tagRegisterRequest.getName());

        Tag registeredTag = tagRepository.save(tag);

        return new TagResponse(registeredTag.getId(),registeredTag.getName());
    }


    @Transactional(readOnly = true)
    public Page<TagResponse> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable)
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build());
    }

    @Transactional
    public TagResponse registerTag(String name) {
        if (tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 태그입니다.");
        }

        Tag saved = tagRepository.save(new Tag(name));
        return TagResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .build();
    }

    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 태그입니다."));

        List<BookTag> bookTags = bookTagRepository.findByTagId(tagId);
        if (!bookTags.isEmpty()) {
            bookTagRepository.deleteAll(bookTags);
        }
        tagRepository.delete(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList();
    }

    @Transactional
    public void addTagToBook(Long bookId, Long tagId) {
        if (bookTagRepository.existsByBookIdAndTagId(bookId, tagId)) {
            return;
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디의 도서를 찾을 수 없습니다.: " + bookId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디의 태그를 찾을 수 없습니다. " + tagId));

        BookTag bookTag = new BookTag(book, tag);
        bookTagRepository.save(bookTag);
    }


    @Transactional
    public void removeTagFromBook(Long bookId, Long tagId) {
        bookTagRepository.deleteByBookIdAndTagId(bookId, tagId);
    }


    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByBookId(Long bookId) {

        List<BookTag> bookTags = bookTagRepository.findByBookId(bookId);


        return bookTags.stream()
                .map(BookTag::getTag)
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }




}
