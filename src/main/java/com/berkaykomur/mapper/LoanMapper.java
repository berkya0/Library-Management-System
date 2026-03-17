package com.berkaykomur.mapper;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.model.Loan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class, MemberMapper.class})
public interface LoanMapper {
    DtoLoan toDtoLoan(Loan savedLoan);
    List<DtoLoan> toDtoLoan(List<Loan> loans);
}
