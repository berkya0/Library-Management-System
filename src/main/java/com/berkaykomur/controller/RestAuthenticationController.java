package com.berkaykomur.controller;

import com.berkaykomur.dto.*;
import com.berkaykomur.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api")
public class RestAuthenticationController  {

	private final IAuthenticationService authenticationService;

	@PostMapping("/user/register")
    @Operation(summary = "Yeni kullanıcı kaydı", description = "Sisteme yeni bir kullanıcı ve bağlı üye profili oluşturur.")
	public ResponseEntity<DtoUser> registerUser(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.registerUser(request));
	}

	@PostMapping("/authenticate")
    @Operation(summary = "Kullanıcı girişi", description = "Kullanıcı adı ve şifre ile giriş yaparak Access ve Refresh Token alır.")
	public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest input) {
		return ResponseEntity.ok(authenticationService.authenticate(input));
	}

	@PostMapping("/refresh-token")
    @Operation(summary = "Token yenileme", description = "Süresi dolan Access Token'ı geçerli bir Refresh Token ile yeniler.")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
		return ResponseEntity.ok(authenticationService.refreshToken(input));
	}

}
