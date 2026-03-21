package com.berkaykomur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "kullanıcı adı boş olamaz!")
    @Size(min = 2,message = "kullanıcı adı en az 2 karakter içermeli!")
    private String username;
    @NotBlank(message = "bir şifre oluşturmalısınız")
    @Size(min = 6,message = "şifre en az 6 karakterden oluşmalı")
    private String password;

    @NotBlank(message = "Ad soyad boş olamaz")
    @Size(max = 100, message = "Ad soyad 100 karakterden uzun olamaz")
    private String fullName;

    @Email(message = "Geçerli bir email adresi giriniz")
    @Size(max = 100, message = "Email 100 karakterden uzun olamaz")
    private String email;

    @NotBlank(message = "telefon numarası girilmelidir")
    @Size(min = 11, max = 11, message = "Telefon numarası 11 haneli olmalıdır")
    private String phoneNumber;
}
