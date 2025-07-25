package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
public interface BookImageService {
    void deleteByBookId(Long bookId);

    void saveBookImage(Book book, MultipartFile file, ImageType type) throws IOException;
}
