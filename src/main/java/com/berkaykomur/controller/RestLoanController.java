// RestLoanControllerImpl.java
package com.berkaykomur.controller;


import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.jwt.CustomUserDetails;
import com.berkaykomur.service.ILoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api/loan")
public class RestLoanController {

    private final ILoanService loanService;

    @PostMapping
    public ResponseEntity<DtoLoan> borrowBook(@Valid @RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.loanBook(request));
    }

    @GetMapping("/my-loans")

    public ResponseEntity<List<DtoLoan>> getMyLoans(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(loanService.getLoansByMemberId(currentUser.getMemberId()));
    }

    @PostMapping("/return/{loanId}")
    public ResponseEntity<DtoLoan> returnBook(@AuthenticationPrincipal CustomUserDetails currentUser,
                                              @PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.returnBook(currentUser.getMemberId(),loanId));
    }
    @GetMapping("/all")
    public ResponseEntity<List<DtoLoan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}