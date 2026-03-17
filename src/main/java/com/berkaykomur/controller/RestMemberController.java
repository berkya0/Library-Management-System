package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.enums.Role;
import com.berkaykomur.jwt.CustomUserDetails;
import com.berkaykomur.service.IMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api/member")
public class RestMemberController {

    private final IMemberService memberService;

    @GetMapping("/get/{id}")
    public ResponseEntity<DtoMember> findMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findMemberById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DtoMember> updateMemberById(@PathVariable Long id, @Valid @RequestBody DtoMemberIU dtoMemberIU) {
        return ResponseEntity.ok(memberService.updateMemberById(id, dtoMemberIU));
    }

    @GetMapping("/get/list")
    public ResponseEntity<List<DtoMember>> findAllMembers() {
        return ResponseEntity.ok(memberService.findAllMembers());
    }

    @GetMapping("/me")
    public ResponseEntity<DtoMember> getCurrentUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(memberService.findMemberByUsername(userDetails.getUsername()));
    }

    @PutMapping("/update-role/{id}")
    public ResponseEntity<DtoMember> updateMemberRole(@PathVariable Long id,@RequestBody Role role) {
       return ResponseEntity.ok(memberService.updateMemberRole(id, role));
    }
}


