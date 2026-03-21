package com.berkaykomur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

	@NotBlank(message = "kullanıcı adı girilmedi")
	private String username;

	@NotBlank(message = "şifre girilmedi")
	private String password;
	
	
	
}
