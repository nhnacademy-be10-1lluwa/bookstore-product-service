package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.response.BestSellerResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    BookResponseMapper INSTANCE = Mappers.getMapper(BookResponseMapper.class);

    //Book Entity -> BookExternalResponseDto
    @Mapping(source = "publishedDate",target = "pubDate")
    @Mapping(source = "regularPrice",target = "priceStandard")
    @Mapping(source = "salePrice",target = "priceSales")
    BookExternalResponse toBookExternalResponse(Book bookEntity);

    //Book Entity -> BestSellerResponseDto
    BestSellerResponse toBestSellerResponse(Book book);

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
                book.getRegularPrice(),
                book.getSalePrice(),
                book.getBookExtraInfo() != null && book.getBookExtraInfo().isGiftwrap(),
                thumbnailUrl
        );
    }

    List<BookDetailResponse> toBookDetailListResponse(List<Book> bookList);

    default Page<BestSellerResponse> toBestSellerPageResponse(Page<Book> bookPage) {
        List<BestSellerResponse> content = bookPage.getContent().stream()
                .map(this::toBestSellerResponse)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                content,
                bookPage.getPageable(),
                bookPage.getTotalElements()
        );
    }


}