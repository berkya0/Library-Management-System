package com.berkaykomur.dto;

import com.berkaykomur.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DtoUserIU {

    @NotBlank
    @Size(min = 2)
	private String username;
    @NotBlank
	private String password;
    @NotBlank
	private Role role;
    @NotBlank
	private DtoMember member;

}
