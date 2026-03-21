package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;
import com.berkaykomur.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api/user")
public class RestUserController {

	private final IUserService userService;
	@PutMapping("/update")
    @Operation(summary = "Kullanıcı şifresini günceller", description = "Giriş yapmış olan kullanıcı kullanıcı adı bilgisiyle şifresini günceller.")
	public ResponseEntity<DtoUser> updateUser(@Valid @RequestBody DtoUserIU dtoUserIU) {
		return ResponseEntity.ok(userService.updateUser(dtoUserIU));
	}
	@DeleteMapping("/delete/{id}")
    @Operation(summary = "Kullanıcı hesabını sil (ADMIN)", description = "Admin belirtilen ID'ye sahip kullanıcıyı veya kullanıcı kendine bağlı tüm verilerini sistemden kalıcı olarak siler.")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
	    userService.deleteUserById(id);
	    return ResponseEntity.ok("User deleted successfully");
	}

}




