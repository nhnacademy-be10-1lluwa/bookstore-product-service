package com.nhnacademy.illuwa.d_book.tag.repository;

import com.nhnacademy.illuwa.d_book.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    boolean existsByName(String tagName);

    void delete(Tag tag);

}
