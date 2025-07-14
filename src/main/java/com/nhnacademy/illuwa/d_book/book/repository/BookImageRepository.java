package com.nhnacademy.illuwa.d_book.book.repository;

import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookImageRepository extends JpaRepository<BookImage,Long> {
    void deleteAllByBook_Id(Long bookId);
    List<BookImage> findByBookId(Long bookId);
    Optional<BookImage> findByBookIdAndImageType(Long bookId, ImageType imageType);
}
