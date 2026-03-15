package com.berkaykomur.controller;

import com.berkaykomur.dto.*;
import com.berkaykomur.service.IAuthenticationService;
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
	public ResponseEntity<DtoUser> registerUser(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.registerUser(request));
	}

	@PostMapping("/admin/register")
	public ResponseEntity<DtoUser>registerAdmin(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.registerAdmin(request));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponse> authentica(@Valid @RequestBody AuthRequest input) {
		return ResponseEntity.ok(authenticationService.authenticate(input));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
		return ResponseEntity.ok(authenticationService.refreshToken(input));
	}

}
