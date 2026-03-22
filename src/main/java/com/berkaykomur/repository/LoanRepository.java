package com.berkaykomur.repository;

import com.berkaykomur.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	  List<Loan> findByMemberIdAndReturnDateIsNull(Long memberId);
      int countByMemberIdAndReturnDateIsNull(Long memberId);

      @Query("select count(l) from Loan l where l.member.id=:memberId and l.dueDate<:today " +
              "and l.returnDate is null")
      int countOverDueLoans(@Param("memberId")Long memberId, @Param("today") LocalDate today);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM loan", nativeQuery = true)
    void hardDeleteAll();

}
