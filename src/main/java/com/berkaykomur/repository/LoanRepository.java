package com.berkaykomur.repository;

import com.berkaykomur.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	  List<Loan> findByMemberId(Long memberId);

}
