package com.berkaykomur.dto;

import com.berkaykomur.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {
    @NotBlank(message = "rol seçmediniz")
    private Role role;

}
