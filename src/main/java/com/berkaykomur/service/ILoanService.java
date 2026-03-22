package com.berkaykomur.service;

import java.util.List;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;

public interface ILoanService {

    DtoLoan loanBook(LoanRequest request,Long memberId);
    List<DtoLoan> getLoansByMemberId(Long memberId);
    DtoLoan returnBook(Long memberId, Long loanId);
    List<DtoLoan> getAllLoans();
}
