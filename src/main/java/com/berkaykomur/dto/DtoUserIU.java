package com.berkaykomur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DtoUserIU {

    @NotBlank(message = "kullanıcı adı girilmedi")
    private String username;

    @NotBlank(message = "şifre girilmedi")
    @Size(min = 6,message = "şifre en az 6 karakterden oluşmalı")
    private String password;

}
