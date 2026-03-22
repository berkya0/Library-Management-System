package com.berkaykomur.service.impl;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.mapper.LoanMapper;
import com.berkaykomur.model.Book;
import com.berkaykomur.model.Loan;
import com.berkaykomur.model.Member;
import com.berkaykomur.repository.BookRepository;
import com.berkaykomur.repository.LoanRepository;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Book mockBook;
    private Member mockMember;
    private LoanRequest loanRequest;
    private Loan mockLoan;
    private Long memberId=10L;


    @BeforeEach
    void setUp() {

        mockBook = TestDataFactory.createTestBook();
        mockBook.setId(1L);

        mockMember = TestDataFactory.createTestMember();
        mockMember.setId(10L);
        mockLoan=TestDataFactory.createTestLoan();

        loanRequest = new LoanRequest();
        loanRequest.setBookId(1L);

    }

    @Test
    void loanBook_Success_ShouldReturnDtoLoan() {

        when(bookRepository.findById(loanRequest.getBookId())).thenReturn(Optional.of(mockBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(loanRepository.countOverDueLoans(eq(memberId), any(LocalDate.class))).thenReturn(0);
        when(loanRepository.countByMemberIdAndReturnDateIsNull(memberId)).thenReturn(2);

        Loan savedLoan = new Loan();
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(loanMapper.toDtoLoan(any(Loan.class))).thenReturn(new DtoLoan());

        DtoLoan result = loanService.loanBook(loanRequest,memberId);

        assertNotNull(result);
        assertFalse(mockBook.isAvailable());

        verify(bookRepository).save(mockBook);
        verify(loanRepository).save(any(Loan.class));
        verify(loanRepository).countOverDueLoans(mockMember.getId(), LocalDate.now());
        verify(loanRepository).countByMemberIdAndReturnDateIsNull(mockMember.getId());
        verify(loanMapper).toDtoLoan(savedLoan);
    }

    @Test
    void loanBook_NoRecordExists_ShouldThrowBaseException() {
        when(bookRepository.findById(loanRequest.getBookId())).thenReturn(Optional.empty());
        BaseException exception=assertThrows(BaseException.class,()->
                loanService.loanBook(loanRequest,memberId));
        String exceptedError=MessagesType.NO_RECORD_EXIST.getErrorMessage()+" : "+mockBook.getId().toString();
        assertEquals(exceptedError,exception.getMessage());
        assertTrue(mockBook.isAvailable());
        verify(bookRepository, never()).save(any());
        verifyNoInteractions(loanRepository,memberRepository,loanMapper);
    }

    @Test
    void loanBook_BookNotAvailable_ShouldThrowBaseException() {
        Long bookId=1L;
        mockBook.setAvailable(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));
        BaseException exception = assertThrows(BaseException.class, () ->
                loanService.loanBook(loanRequest,memberId)
        );
        assertEquals(MessagesType.ALREADY_LOANED.getErrorMessage()+" : "+loanRequest.getBookId().toString(),exception.getMessage());
        verifyNoInteractions(memberRepository, loanRepository,loanMapper);
        verify(bookRepository, never()).save(any());
    }
    @Test
    void loanBook_MemberNotFound_ShouldThrowBaseException() {

        when(bookRepository.findById(loanRequest.getBookId())).thenReturn(Optional.of(mockBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        BaseException exception=assertThrows(BaseException.class,()->
                loanService.loanBook(loanRequest,memberId));
        String exceptedError=MessagesType.MEMBER_NOT_FOUND.getErrorMessage()+" : "+mockMember.getId().toString();
        assertEquals(exceptedError,exception.getMessage());
        assertTrue(mockBook.isAvailable());
        verify(bookRepository, never()).save(any());
        verifyNoInteractions(loanRepository,loanMapper);
    }
    @Test
    void loanBook_OverDueLimitExceeded_ShouldThrowBaseException() {
      
        when(bookRepository.findById(loanRequest.getBookId())).thenReturn(Optional.of(mockBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(loanRepository.countOverDueLoans(eq(memberId), any(LocalDate.class))).thenReturn(1);

        BaseException baseException= assertThrows(BaseException.class, () ->
                loanService.loanBook(loanRequest,memberId));
        String exceptedError=MessagesType.OVERDUE_LIMIT_EXCEEDED.getErrorMessage();
        assertEquals(exceptedError,baseException.getMessage());
        verify(loanRepository, never()).countByMemberIdAndReturnDateIsNull(any());
        assertTrue(mockBook.isAvailable());
        verify(loanRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
        verifyNoInteractions(loanMapper);
    }

    @Test
    void loanBook_MaxLimitExceeded_ShouldThrowBaseException() {

        when(bookRepository.findById(loanRequest.getBookId())).thenReturn(Optional.of(mockBook));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(loanRepository.countOverDueLoans(eq(memberId), any(LocalDate.class))).thenReturn(0);
        when(loanRepository.countByMemberIdAndReturnDateIsNull(memberId)).thenReturn(5);

        BaseException baseException= assertThrows(BaseException.class, () ->
                loanService.loanBook(loanRequest,memberId));
        String exceptedError=MessagesType.MAX_BOOK_LIMIT_EXCEEDED.getErrorMessage()+" : "+
                "Mevcut kitap sayın: "+5;
        assertEquals(exceptedError,baseException.getMessage());
        assertTrue(mockBook.isAvailable());
        verify(loanRepository).countOverDueLoans(mockMember.getId(), LocalDate.now());
        verify(loanRepository).countByMemberIdAndReturnDateIsNull(mockMember.getId());
        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void returnBook_Success_ShouldMakeBookAvailable() {
        Loan existingLoan = new Loan();
        existingLoan.setId(100L);
        existingLoan.setMember(mockMember);
        existingLoan.setBook(mockBook);
        mockBook.setAvailable(false);

        when(loanRepository.findById(existingLoan.getId())).thenReturn(Optional.of(existingLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);
        when(loanMapper.toDtoLoan(any(Loan.class))).thenReturn(new DtoLoan());

        DtoLoan result = loanService.returnBook(memberId, existingLoan.getId());
        assertNotNull(result);
        assertTrue(mockBook.isAvailable());
        assertNotNull(existingLoan.getReturnDate());
        assertEquals(existingLoan.getReturnDate(),LocalDate.now());
        verify(bookRepository).save(mockBook);
        verify(loanRepository).save(existingLoan);
        verify(loanMapper).toDtoLoan(existingLoan);

    }
    @Test
    void returnBook_NoRecordExist_ShouldThrowBaseException() {
        Long loanId=100L;
        mockBook.setAvailable(false);

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());
        BaseException exception=assertThrows(BaseException.class,
                ()->loanService.returnBook(memberId, loanId));
        String exceptedError=MessagesType.NO_RECORD_EXIST.getErrorMessage()+" : "+loanId;
        assertEquals(exceptedError,exception.getMessage());
        assertFalse(mockBook.isAvailable());
        verify(loanRepository, never()).save(any());
        verifyNoInteractions(loanMapper,bookRepository);
    }
    @Test
    void getLoansByMemberId_Success_ShouldReturnLoans() {
        Long memberId=10L;
        List<Loan> mockLoans = List.of(mockLoan, new Loan());
        when(loanRepository.findByMemberIdAndReturnDateIsNull(memberId)).thenReturn(mockLoans);
        when(loanMapper.toDtoLoan(mockLoans)).thenReturn(List.of(new DtoLoan(), new DtoLoan()));

        List<DtoLoan> result = loanService.getLoansByMemberId(memberId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loanRepository).findByMemberIdAndReturnDateIsNull(memberId);
        verify(loanMapper).toDtoLoan(mockLoans);
    }

    @Test
    void returnBook_UnauthorizedMember_ShouldThrowBaseException() {
        Loan existingLoan = new Loan();
        existingLoan.setId(100L);
        Member anotherMember = new Member();
        anotherMember.setId(99L);
        existingLoan.setMember(anotherMember);
        mockBook.setAvailable(false);
        when(loanRepository.findById(existingLoan.getId())).thenReturn(Optional.of(existingLoan));

        BaseException exception= assertThrows(BaseException.class,
                () -> loanService.returnBook(10L, existingLoan.getId()));
        String expectedError=MessagesType.UNAUTHORIZED_ACTION.getErrorMessage();
        assertEquals(expectedError,exception.getMessage());
        assertFalse(mockBook.isAvailable());
        verifyNoInteractions(loanMapper,bookRepository);
    }
    @Test
    void getAllLoans_ShouldReturnAllLoans() {
        List<Loan> mockLoans = List.of(new Loan(), new Loan());
        when(loanRepository.findAll()).thenReturn(mockLoans);
        when(loanMapper.toDtoLoan(anyList())).thenReturn(List.of(new DtoLoan(), new DtoLoan()));
        List<DtoLoan> result = loanService.getAllLoans();
        assertEquals(2, result.size());
        verify(loanMapper).toDtoLoan(anyList());
    }
}