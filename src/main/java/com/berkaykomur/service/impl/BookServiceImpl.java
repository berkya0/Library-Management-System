package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.mapper.BookMapper;
import com.berkaykomur.model.Book;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService{

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional(readOnly=true)
    public List<DtoBook> findAllBooks() {
        List<Book> allBook = bookRepository.findAll();
        return bookMapper.toDtoBookList(allBook);
    }
    @Override
    public DtoBook findBookById(Long id) {
       Book book = bookRepository.findById(id)
               .orElseThrow(()->new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST,id.toString())));
        return bookMapper.toDtoBook(book);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DtoBook saveBook(DtoBookIU dtoBookIU) {
        Book saveBook = bookMapper.toBook(dtoBookIU);
        saveBook.setAvailable(true);
        return bookMapper.toDtoBook(bookRepository.save(saveBook));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));
        if (!book.isAvailable()) {
            throw new BaseException(new ErrorMessage(MessagesType.GENERAL_EXCEPTION,
                    "Ödünç alınmış kitap silinemez. Önce kitabın iade edilmesi gerekir."));
        }
        book.setActive(false);
        bookRepository.save(book);
    }

}