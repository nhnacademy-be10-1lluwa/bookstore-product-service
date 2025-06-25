package com.nhnacademy.illuwa.d_book.book.mapper;

import com.nhnacademy.illuwa.d_book.book.dto.BookRegisterRequest;
import com.nhnacademy.illuwa.d_book.book.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {


    BookExternalMapper INSTANCE =  Mappers.getMapper(BookExternalMapper.class);


    //dto -> Entity
    @Mapping(source = "pubDate",target = "publishedDate")
    Book toBookEntity(BookRegisterRequest bookRegisterRequest);
}
