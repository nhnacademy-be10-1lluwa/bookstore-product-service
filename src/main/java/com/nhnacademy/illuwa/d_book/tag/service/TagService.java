package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.dto.TagResponse;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.tag.exception.TagNotFoundException;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TagService {

    TagRepository tagRepository;



    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
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

    @Transactional
    public void deleteTag(Long tagId) {

        Optional<Tag> tagById = tagRepository.findById(tagId);

        if(tagById.isEmpty()){
            throw new TagNotFoundException("존재하지 않는 태그입니다.");
        }

        Tag targetTag = tagById.get();
        tagRepository.delete(targetTag);
    }

    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }
}
