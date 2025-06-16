package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.BookDetailResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    BookResponseMapper INSTANCE = Mappers.getMapper(BookResponseMapper.class);


    //Book Entity -> BookExternalResponseDto
    @Mapping(source = "publishedDate",target = "pubDate")
    @Mapping(source = "regularPrice",target = "priceStandard")
    @Mapping(source = "salePrice",target = "priceSales")
    @Mapping(source = "imgUrl", target = "cover")
    @Mapping(source = "category", target = "categoryName")
    BookExternalResponse toBookExternalResponse(Book bookEntity);

    //Book Entity -> BookDetailResponseDto
    BookDetailResponse toBookDetailResponse(Book bookEntity);


}
