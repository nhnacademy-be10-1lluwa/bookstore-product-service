package com.nhnacademy.illuwa.d_book.book.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookExternalMapper {

    BookExternalMapper INSTANCE =  Mappers.getMapper(BookExternalMapper.class);


}
