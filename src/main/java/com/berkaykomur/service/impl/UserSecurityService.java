package com.berkaykomur.service.impl;

import com.berkaykomur.jwt.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurityService")
public class UserSecurityService {

    public boolean isOwner(Authentication authentication, Long memberId) {
        if (authentication == null || memberId == null) {
            return false;
        }
        CustomUserDetails myUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return myUserDetails.getMemberId().equals(memberId);
    }
}
