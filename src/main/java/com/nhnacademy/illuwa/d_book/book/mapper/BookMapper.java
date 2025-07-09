package com.nhnacademy.illuwa.d_book.book.mapper;


import com.nhnacademy.illuwa.d_book.book.dto.response.BookExternalResponse;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.FinalAladinBookRegisterRequest;
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



    @Mapping(source = "pubDate", target = "publishedDate")
    @Mapping(source = "regularPrice", target = "regularPrice")
    @Mapping(source = "salePrice", target = "salePrice")
    @Mapping(source = "contents", target = "contents")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "publisher", target = "publisher")
    @Mapping(source = "isbn", target = "isbn")
    Book fromFinalAladinRequest(FinalAladinBookRegisterRequest request);

}
