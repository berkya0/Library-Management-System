package com.berkaykomur.service.impl;

import com.berkaykomur.dto.*;
import com.berkaykomur.enums.Role;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.jwt.CustomUserDetails;
import com.berkaykomur.jwt.JwtService;
import com.berkaykomur.mapper.UserMapper;
import com.berkaykomur.model.Member;
import com.berkaykomur.model.RefreshToken;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.RefreshTokenRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService implements IAuthenticationService {


    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private User createUser(RegisterRequest request) {
        if (userRepository.existsByUsernameAndIsActiveTrue(request.getUsername())) {
            throw new BaseException(new ErrorMessage((MessagesType.USERNAME_ALREADY_TAKEN), request.getUsername()));
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(new Date());
        user.setRole(Role.USER);

        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setFullName(request.getFullName());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setUser(user);
        user.setMember(member);
        return user;
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreateTime(new Date());
        refreshToken.setUser(user);
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + jwtService.refreshTokenExpiration));
        refreshToken.setRefreshToken(UUID.randomUUID().toString()); // Rastgele token üretimi
        return refreshToken;
    }

    private boolean isRefreshTokenValid(Date expireDate) {
        return new Date().before(expireDate);
    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshTokenAndIsActiveTrue(request.getRefreshToken())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_INVALID, "")));

        if (!isRefreshTokenValid(refreshToken.getExpiredDate())) {
            throw new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_IS_EXPIRED, ""));
        }
        User user = refreshToken.getUser();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(customUserDetails);
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken);
        RefreshToken newRefreshToken = refreshTokenRepository.save(createRefreshToken(user));

        return new AuthResponse(
                accessToken,
                newRefreshToken.getRefreshToken(),
                user.getUsername(),
                user.getRole(),
                jwtService.accsessTokenExpiration
        );
    }

    @Override
    public DtoUser registerUser(RegisterRequest request) {
        User savedUser = userRepository.save(createUser(request));
        return userMapper.toDtoUser(savedUser);
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenRepository.save(createRefreshToken(userDetails.getUser()));
            return new AuthResponse(
                    accessToken,
                    refreshToken.getRefreshToken(),
                    userDetails.getUsername(),
                    userDetails.getRole(),
                    jwtService.accsessTokenExpiration
            );
        }catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            e.printStackTrace();
        throw new BaseException(new ErrorMessage(MessagesType.USERNAME_OR_PASSWORD_INVALID, null));
    } catch (Exception e) {
            e.printStackTrace();

        throw new BaseException(new ErrorMessage(MessagesType.GENERAL_EXCEPTION, e.getMessage()));
    }
    }

}