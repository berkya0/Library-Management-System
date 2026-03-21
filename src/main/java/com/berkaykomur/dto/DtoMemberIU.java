package com.berkaykomur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoMemberIU {
    @NotBlank(message = "Ad soyad boş olamaz")
    @Size(max = 100, message = "Ad soyad 100 karakterden uzun olamaz")
    private String fullName;

    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 100, message = "Email 100 karakterden uzun olamaz")
    private String email;
    
    @NotBlank(message = "telefon numarası girilmedi")
    @Size(min = 11, max = 11, message = "Telefon numarası 11 haneli olmalıdır")
    private String phoneNumber;
}