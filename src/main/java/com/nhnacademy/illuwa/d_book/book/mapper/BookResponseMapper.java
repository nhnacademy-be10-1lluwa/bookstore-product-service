package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;


@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    BookResponseMapper INSTANCE = Mappers.getMapper(BookResponseMapper.class);

    //Book Entity -> BookExternalResponseDto
    @Mapping(source = "publishedDate",target = "pubDate")
    @Mapping(source = "regularPrice",target = "priceStandard")
    @Mapping(source = "salePrice",target = "priceSales")
    BookExternalResponse toBookExternalResponse(Book bookEntity);

    default BookDetailResponse toBookDetailResponse(Book book) {
        if (book == null) return null;

        String thumbnailUrl = book.getBookImages().stream()
                .filter(img -> img.getImageType() == ImageType.THUMBNAIL)
                .map(BookImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return new BookDetailResponse(
                book.getId(),
                book.getTitle(),
                book.getContents(),
                book.getDescription(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate(),
                book.getIsbn(),
                book.getRegularPrice().intValue(),
                book.getSalePrice().intValue(),
                book.getBookExtraInfo() != null && book.getBookExtraInfo().isGiftwrap(),
                thumbnailUrl
        );
    }



    List<BookDetailResponse> toBookDetailListResponse(List<Book> bookList);

}
