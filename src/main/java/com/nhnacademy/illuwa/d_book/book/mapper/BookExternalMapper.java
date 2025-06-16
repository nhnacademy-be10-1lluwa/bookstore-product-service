package com.nhnacademy.illuwa.d_book.book.mapper;


import com.nhnacademy.illuwa.d_book.book.dto.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookExternalMapper {

    BookExternalMapper INSTANCE =  Mappers.getMapper(BookExternalMapper.class);

    @Mapping(source = "pubDate",target = "publishedDate")
    @Mapping(source = "priceStandard", target = "regularPrice")
    @Mapping(source = "priceSales", target = "salePrice")
    @Mapping(source = "cover", target = "imgUrl")
    @Mapping(source = "categoryName", target = "category")
    @Mapping(target = "giftWrap", expression = "java(true)")
    @Mapping(target = "contents", constant = "")
    Book toBookEntity(BookExternalResponse responseDto);

}
