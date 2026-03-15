package com.berkaykomur.service;

import java.util.List;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;

public interface ILoanService {

    DtoLoan loanBook(LoanRequest request);
    List<DtoLoan> getLoansByMemberId(Long memberId);
    DtoLoan returnBook(Long memberId, Long loanId);
   // boolean isLoanBelongsToMember(Long loanId, Long memberId);
    List<DtoLoan> getAllLoans();
}
