package com.nhnacademy.illuwa.d_book.book.service;

import com.nhnacademy.illuwa.d_book.book.repository.BookImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookImageService {
    private final BookImageRepository bookImageRepository;

    @Transactional
    public void deleteByBookId(Long bookId) {
        bookImageRepository.deleteAllByBook_Id(bookId);
    }

}
