package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.Book;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.LoanRepository;
import com.berkaykomur.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService{

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly=true)
    public List<DtoBook> findAllBooks() {
        List<Book> allBook = bookRepository.findAll();
        List<DtoBook> dtoList=new ArrayList<>();
        for (Book book : allBook) {
            DtoBook dtoBook=new DtoBook();
            BeanUtils.copyProperties(book, dtoBook);
            dtoList.add(dtoBook);
        }
        return dtoList;
    }
    @Override
    public DtoBook findBookById(Long id) {
        Optional<Book> optional = bookRepository.findById(id);
        if(optional.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST,id.toString()));
        }
        DtoBook dtoBook=new DtoBook();
        BeanUtils.copyProperties(optional.get(), dtoBook);
        return dtoBook;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DtoBook saveBook(DtoBookIU dtoBookIU) {
        Book book = new Book();
        book.setTitle(dtoBookIU.getTitle());
        book.setAuthor(dtoBookIU.getAuthor());
        book.setCategory(dtoBookIU.getCategory());
        book.setIsbnNo(dtoBookIU.getIsbnNo());
        book.setAvailable(true);
        book = bookRepository.save(book);
        DtoBook dtoBook = new DtoBook();
        BeanUtils.copyProperties(book, dtoBook);
        return dtoBook;
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
        loanRepository.deleteByBookId(id);
        bookRepository.delete(book);
    }

}