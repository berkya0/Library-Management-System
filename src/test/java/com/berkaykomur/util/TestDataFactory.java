package com.berkaykomur.util;

import com.berkaykomur.model.Book;
import com.berkaykomur.model.Category;
import com.berkaykomur.model.Loan;
import com.berkaykomur.model.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class TestDataFactory {

    public static Book createTestBook() {
        Book mockBook = new Book();
        mockBook.setAvailable(true);
        mockBook.setActive(true);
        mockBook.setAuthor("Author");
        mockBook.setIsbnNo("ISBN-" + System.nanoTime());
        mockBook.setCategory(Category.ROMAN);
        mockBook.setTitle("Title");
        return mockBook;
    }

    public static Member createTestMember() {
        Member mockMember = new Member();

        mockMember.setEmail("test-" + System.nanoTime() + "@email.com");
        mockMember.setFullName("Full Name");
        mockMember.setActive(true);
        mockMember.setMembershipDate(Date.from(Instant.now()));
        String randomPhone = "0505" + (System.nanoTime() % 10000000);
        mockMember.setPhoneNumber(randomPhone);
        return mockMember;
    }
    public static Loan  createTestLoan(){
        Loan mockLoan = new Loan();
        mockLoan.setBook(createTestBook());
        mockLoan.setMember(createTestMember());
        mockLoan.setActive(true);
        mockLoan.setLoanDetails();
        return mockLoan;
    }

    public static void mySecurtityContext(Long currentMemberId){
        var customPrincipal = new Object() {
            public Long getMemberId() { return currentMemberId; }
        };

        Authentication auth = new UsernamePasswordAuthenticationToken(
                customPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
