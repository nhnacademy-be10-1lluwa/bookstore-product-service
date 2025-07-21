package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.response.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.response.SimpleBookResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import com.nhnacademy.illuwa.d_book.book.entity.BookImage;
import com.nhnacademy.illuwa.d_book.book.enums.ImageType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import java.util.List;


@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    //Book Entity -> BookExternalResponseDto
    @Mapping(source = "publishedDate",target = "pubDate")
    @Mapping(source = "regularPrice",target = "priceStandard")
    @Mapping(source = "salePrice",target = "priceSales")
    BookExternalResponse toExternalResponse(Book bookEntity);

    //Book Entity -> SimpleBookResponseDto
    @Mapping(source = "bookImages",target = "cover", qualifiedByName = "firstImageUrl")
    SimpleBookResponse toSimpleBookResponse(Book book);

    @Named("firstImageUrl")
    default String getFirstImageUrl(List<BookImage> bookImages) {
        if (bookImages != null && !bookImages.isEmpty() && bookImages.getFirst() != null) {
            return bookImages.getFirst().getImageUrl();
        }
        return null;
    }

    @Mapping(target = "imageUrls", source  = "bookImages", qualifiedByName = "toImageUrlList")
    @Mapping(target = "count", source  = "bookExtraInfo.count", defaultValue = "0")
    @Mapping(target = "status", source  = "bookExtraInfo.status")
    @Mapping(target = "giftWrap", source  = "bookExtraInfo.giftWrap", defaultValue = "false")
    BookDetailResponse toBookDetailResponse(Book book);


    @Named("toImageUrlList")
    default List<String> toImageUrlList(List<BookImage> images) {
        return images == null ? List.of()
                : images.stream()
                .filter(i -> i.getImageType() == ImageType.THUMBNAIL)
                .map(BookImage::getImageUrl)
                .toList();
    }

    List<BookDetailResponse> toBookDetailListResponse(List<Book> bookList);

    default Page<SimpleBookResponse> toSimpleBookPageResponse(Page<Book> bookPage) {
        List<SimpleBookResponse> content = bookPage.getContent().stream()
                .map(this::toSimpleBookResponse)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                content,
                bookPage.getPageable(),
                bookPage.getTotalElements()
        );
    }

}