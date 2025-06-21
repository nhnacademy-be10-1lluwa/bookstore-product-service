package com.nhnacademy.illuwa.d_book.tag.service;

import com.nhnacademy.illuwa.d_book.tag.dto.TagRegisterRequest;
import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import com.nhnacademy.illuwa.d_book.tag.exception.TagAlreadyExistsException;
import com.nhnacademy.illuwa.d_book.tag.exception.TagNotFoundException;
import com.nhnacademy.illuwa.d_book.tag.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TagService {

    TagRepository tagRepository;


    public TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    public void registerTag(TagRegisterRequest tagRegisterRequest) {

        String tagName = tagRegisterRequest.getName();

        if(tagRepository.existsByName(tagName)){
            throw new TagAlreadyExistsException("이미 존재하는 태그입니다.");
        }

        Tag tag = new Tag();
        tag.setName(tagName);
        tagRepository.save(tag);
    }

    public void deleteTag(Long tagId) {

        Optional<Tag> tagById = tagRepository.findById(tagId);

        if(tagById.isEmpty()){
            throw new TagNotFoundException("존재하지 않는 태그입니다.");

        }
        tagRepository.delete(tagById);
    }
}
