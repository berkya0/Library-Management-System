
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

    private static final int MAX_BOOK_LIMIT=5;
    
    @Override
    @Transactional
    @PreAuthorize("#request.memberId==authentication.principal.memberId")
    public DtoLoan loanBook(LoanRequest request) {
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.NO_RECORD_EXIST,request.getBookId().toString())));

        if (!book.isAvailable()) {
            throw new BaseException(
                new ErrorMessage(MessagesType.ALREADY_LOANED, request.getBookId().toString()));
        }

        Member member = memberRepository.findById(request.getMemberId())
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.MEMBER_NOT_FOUND, request.getMemberId().toString())));

        int overLoanCount=loanRepository.countOverDueLoans(member.getId(), LocalDate.now());
        if(overLoanCount>0){
            throw new BaseException(new ErrorMessage(MessagesType.OVERDUE_LIMIT_EXCEEDED,null));
        }
        int totalLoan=loanRepository.countByMemberIdAndReturnDateIsNull(member.getId());
        if(totalLoan>=MAX_BOOK_LIMIT) {
            throw new BaseException(new ErrorMessage(MessagesType.MAX_BOOK_LIMIT_EXCEEDED,"Mevcut kitap sayın: "+totalLoan));
        }
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
                .filter(l->l.getMember().getId().equals(memberId))
            .orElseThrow(() -> new BaseException(
                new ErrorMessage(MessagesType.NO_RECORD_EXIST, loanId.toString())));

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