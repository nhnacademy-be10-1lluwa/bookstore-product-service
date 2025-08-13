package com.nhnacademy.illuwa.d_book.book.mapper;


import com.nhnacademy.illuwa.d_book.book.dto.request.BookApiRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.dto.request.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "pubDate", target = "publishedDate")
    Book toBookEntity(BookRegisterRequest bookRegisterRequest);

    @Mapping(source = "pubDate", target = "publishedDate")
    @Mapping(source = "count", target = "bookExtraInfo.count")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookImages", ignore = true)
    @Mapping(target = "bookTags", ignore = true)
    Book fromApiRequest(BookApiRegisterRequest request);

}
