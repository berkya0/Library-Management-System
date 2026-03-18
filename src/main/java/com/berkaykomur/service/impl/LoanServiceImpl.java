
package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.mapper.LoanMapper;
import com.berkaykomur.model.Book;
import com.berkaykomur.model.Loan;
import com.berkaykomur.model.Member;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.LoanRepository;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.service.ILoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements ILoanService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    
    @Override
    @Transactional
    @PreAuthorize("#request.memberId==authentication.principal.memberId")
    public DtoLoan loanBook(LoanRequest request) {
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.NO_RECORD_EXIST, 
                "Kitap bulunamadı: " + request.getBookId())));

        if (!book.isAvailable()) {
            throw new BaseException(
                new ErrorMessage(MessagesType.ALREADY_LOANED, 
                "Bu kitap zaten ödünç alınmış"));
        }

        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.NO_RECORD_EXIST,
                "Üye bulunamadı: " + request.getMemberId())));

        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setLoanDetails();
        return loanMapper.toDtoLoan(loanRepository.save(loan));
    }
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#memberId==authentication.principal.memberId")
    public List<DtoLoan> getLoansByMemberId(Long memberId) {
        List<Loan> loans = loanRepository.findByMemberId(memberId);
       return loanMapper.toDtoLoan(loans);
    }

    @Override
    @Transactional
    @PreAuthorize("#memberId == authentication.principal.memberId")
    public DtoLoan returnBook(Long memberId, Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.NO_RECORD_EXIST, 
                "Ödünç kaydı bulunamadı: " + loanId)));

        Book book = loan.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        loan.setReturnDate(LocalDate.now());
        return loanMapper.toDtoLoan(loanRepository.save(loan));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<DtoLoan> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loanMapper.toDtoLoan(loans);
    }
}