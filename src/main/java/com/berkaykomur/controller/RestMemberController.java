package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.dto.UpdateRoleRequest;
import com.berkaykomur.enums.Role;
import com.berkaykomur.jwt.CustomUserDetails;
import com.berkaykomur.service.IMemberService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "ID ile üye bul", description = "Belirtilen ID numarasına sahip üyenin detaylı bilgilerini getirir.")
    public ResponseEntity<DtoMember> findMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findMemberById(id));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Üye bilgilerini güncelle", description = "Belirli bir üyenin profil bilgilerini (isim, telefon vb.) günceller.")
    public ResponseEntity<DtoMember> updateMemberById(@PathVariable Long id, @Valid @RequestBody DtoMemberIU dtoMemberIU) {
        return ResponseEntity.ok(memberService.updateMemberById(id, dtoMemberIU));
    }

    @GetMapping("/get/list")
    @Operation(summary = "Tüm üyeleri listele (ADMIN)", description = "Sistemde kayıtlı olan tüm üyelerin listesini getirir.")
    public ResponseEntity<List<DtoMember>> findAllMembers() {
        return ResponseEntity.ok(memberService.findAllMembers());
    }

    @GetMapping("/me")
    @Operation(summary = "Profil bilgilerim", description = "Giriş yapmış kullanıcının kendi üye bilgilerini getirir.")
    public ResponseEntity<DtoMember> getCurrentUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(memberService.findMemberByUsername(userDetails.getUsername()));
    }

    @PutMapping("/update-role/{id}")
    @Operation(summary = "Kullanıcı rolü güncelle (ADMIN)", description = "Bir üyenin yetki seviyesini (USER/ADMIN) değiştirir.")
    public ResponseEntity<DtoMember> updateMemberRole(@PathVariable Long id,@RequestBody UpdateRoleRequest role) {
       return ResponseEntity.ok(memberService.updateMemberRole(id, role));
    }
}


