package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api/book")
public class RestBookController {

	private final IBookService bookService;

	@GetMapping("/get/list")
    @Operation(summary = "Tüm kitapları listele", description = "Sistemdeki tüm aktif ve mevcut kitapları getirir.")
	public ResponseEntity<List<DtoBook>> findAllBooks() {
		return ResponseEntity.ok(bookService.findAllBooks());
	}

	@GetMapping("/get/{bookId}")
    @Operation(summary = "ID ile kitap ara", description = "Belirtilen ID'ye sahip kitabın detaylarını getirir.")
	public ResponseEntity<DtoBook>findBookById(@PathVariable Long bookId) {
		return ResponseEntity.ok(bookService.findBookById(bookId));
	}

    @DeleteMapping("/delete/{bookId}")
    @Operation(summary = "Kitap sil (ADMIN)", description = "Belirtilen kitabı sistemden kaldırır. Sadece ADMIN yetkisiyle çalışır.")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok("Book deleted successfully");
    }
	@PostMapping("/save")
    @Operation(summary = "Yeni kitap ekle (ADMIN)", description = "Sisteme yeni bir kitap kaydeder.")
	public ResponseEntity<DtoBook> saveBook(@Valid @RequestBody DtoBookIU dtoBookIU) {
	    return ResponseEntity.ok(bookService.saveBook(dtoBookIU));
	}

}




