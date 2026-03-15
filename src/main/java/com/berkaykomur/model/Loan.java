package com.berkaykomur.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "loan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;
    @Column(nullable = false)
    private LocalDate dueDate;

    public void setLoanDetails() {
        this.loanDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(14);
    }
}