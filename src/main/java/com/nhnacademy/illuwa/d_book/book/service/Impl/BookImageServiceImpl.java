package com.nhnacademy.illuwa.d_book.book.service.impl;

import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import com.nhnacademy.illuwa.d_book.book.service.BookImageService;
import com.nhnacademy.illuwa.infra.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {
    private final BookImageRepository bookImageRepository;
    private final MinioStorageService minioStorageService;

    @Override
    @Transactional
    public void deleteByBookId(Long bookId) {
        bookImageRepository.deleteAllByBook_Id(bookId);
    }

    @Override
    public void saveBookImage(Book book, MultipartFile file, ImageType type) throws IOException {
        String imageUrl = minioStorageService.uploadBookImage(file);
        BookImage bookImage = new BookImage(book, imageUrl, type);
        bookImageRepository.save(bookImage);
    }
}
