package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.Book;
import com.berkaykomur.model.Loan;
import com.berkaykomur.model.Member;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.LoanRepository;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.service.ILoanService;
import com.berkaykomur.util.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoanServiceImplIntegrationTest {

    @Autowired
    private ILoanService loanService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private LoanRepository loanRepository;

    private Long bookId;
    private Long memberId;

    @BeforeEach
    void setUp() {
        Book book = TestDataFactory.createTestBook();
        book.setAvailable(true);
        bookRepository.save(book);
        Member member = TestDataFactory.createTestMember();
        memberRepository.save(member);
        bookId = book.getId();
        memberId = member.getId();

        final Long currentMemberId = memberId;
        TestDataFactory.mySecurtityContext(currentMemberId);
    }
    @Test
    void loanBook_Integration_Success() {
        LoanRequest request = new LoanRequest();
        request.setBookId(bookId);
        request.setMemberId(memberId);

        DtoLoan result = loanService.loanBook(request);

        assertNotNull(result);
        assertFalse(bookRepository.findById(bookId).get().isAvailable());
    }
    @Test
    void loanBook_AlreadyLoaned_ShouldThrowException() {
        long loanCountBefore = loanRepository.count();
        Book book = TestDataFactory.createTestBook();
        book.setAvailable(false);
        bookRepository.save(book);

        LoanRequest request = new LoanRequest();
        request.setBookId(book.getId());
        request.setMemberId(memberId);

        BaseException exception = assertThrows(BaseException.class, () ->
                loanService.loanBook(request)
        );

        assertTrue(exception.getMessage().contains(MessagesType.ALREADY_LOANED.getErrorMessage()+" : "+request.getBookId()));
        Book currentBook = bookRepository.findById(book.getId()).get();
        assertFalse(currentBook.isAvailable(), "Kitap durumu değişmemeliydi!");

        long loanCountAfter = loanRepository.count();
        assertEquals(loanCountBefore, loanCountAfter, "Hata durumunda yeni loan kaydı oluşmamalı!");
    }
    @Test
    void loanBook_UnauthorizedMember_ShouldThrowException() {
        Member anotherMember = TestDataFactory.createTestMember();
        memberRepository.save(anotherMember);
        LoanRequest request = new LoanRequest();
        request.setBookId(bookId);
        request.setMemberId(anotherMember.getId());

        assertThrows(AccessDeniedException.class, () ->loanService.loanBook(request));

    }
    @Test
    void returnBook_Integration_Success() {
        Member existingMember = memberRepository.findById(memberId).get();

        Book book = TestDataFactory.createTestBook();
        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = TestDataFactory.createTestLoan();
        loan.setMember(existingMember);
        loan.setBook(book);
        loan.setReturnDate(null);

        loanRepository.save(loan);
        DtoLoan result = loanService.returnBook(existingMember.getId(),loan.getId());
        assertNotNull(result);
        assertTrue(bookRepository.findById(book.getId()).get().isAvailable(), "Kitap iade sonrası true olmalı");
    }
    @Test
    void returnBook_UnAuthorizedMember_ShouldThrowException() {
        Member memberB = TestDataFactory.createTestMember();
        memberRepository.save(memberB);

        Book book = TestDataFactory.createTestBook();
        book.setAvailable(false);
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setMember(memberB);
        loan.setBook(book);
        loan.setReturnDate(null);
        Loan savedLoan = loanRepository.save(loan);

        BaseException exception = assertThrows(BaseException.class, () ->
                loanService.returnBook(memberId, savedLoan.getId())
        );
        assertEquals(MessagesType.UNAUTHORIZED_ACTION.getErrorMessage(), exception.getMessage());
    }
    @Test
    void getAllLoans_NonAdminUser_ShouldThrowAccessDenied() {
        assertThrows(AccessDeniedException.class, () -> loanService.getAllLoans());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}