package com.berkaykomur.service;

import com.berkaykomur.dto.AuthRequest;
import com.berkaykomur.dto.AuthResponse;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.RefreshTokenRequest;
import com.berkaykomur.dto.RegisterRequest;

public interface IAuthenticationService {

    AuthResponse refreshToken(RefreshTokenRequest input);
    DtoUser registerUser(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
   // DtoUser registerAdmin(RegisterRequest request);
	

}
