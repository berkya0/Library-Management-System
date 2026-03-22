package com.berkaykomur.dto;

import com.berkaykomur.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {
    @NotNull(message = "rol seçmediniz")
    private Role role;

}
