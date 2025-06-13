package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.category.repository.CustomizedBookCategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Entity → DTO
    public BookExternalResponse toResponse(Book book) {

        return new BookExternalResponse(
                book.getTitle(),
                book.getAuthor(),
                book.getPublishedDate(),
                book.getDescription(),
                book.getIsbn(),
                book.getSalePrice(),
                book.getRegularPrice(),
                book.getImgUrl(),
                book.getCategory(),
                book.getPublisher()
        );
    }

    // DTO → Entity
    public Book toEntity(BookExternalResponse dto) {

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setContents(null);
        book.setDescription(dto.getDescription());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setPublishedDate(dto.getPubDate());
        book.setIsbn(dto.getIsbn());
        book.setRegularPrice(dto.getPriceStandard());
        book.setSalePrice(dto.getPriceSales());
        book.setGiftWrap(true);
        book.setImgUrl(dto.getCover());
        book.setCategory(dto.getCategoryName());

        return book;
    }
}
