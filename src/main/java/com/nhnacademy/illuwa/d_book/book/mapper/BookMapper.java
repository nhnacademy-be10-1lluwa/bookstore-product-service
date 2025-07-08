package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "pubDate", target = "publishedDate")
    Book toBookEntity(BookRegisterRequest bookRegisterRequest);

    @Mapping(source = "pubDate", target = "publishedDate")
    @Mapping(source = "priceStandard", target = "regularPrice")
    @Mapping(source = "priceSales", target = "salePrice")
    @Mapping(target = "id", ignore = true) // id는 자동 생성되므로 매핑에서 제외
    Book fromAladinResponse(BookExternalResponse response);
}
