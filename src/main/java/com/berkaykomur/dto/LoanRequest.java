// LoanRequest.java
package com.berkaykomur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    @NotBlank(message = "Kitap ID boş olamaz")
    private Long bookId;
    
    @NotBlank(message = "Üye ID boş olamaz")
    private Long memberId;
}