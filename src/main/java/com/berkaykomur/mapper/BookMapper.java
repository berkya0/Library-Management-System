package com.berkaykomur.mapper;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.model.Book;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toBook(DtoBookIU bookIU);
    List<DtoBook> toDtoBookList(List<Book> bookList);
    DtoBook toDtoBook(Book book);

}
