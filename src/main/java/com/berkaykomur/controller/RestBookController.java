package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.service.IBookService;
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
	public ResponseEntity<List<DtoBook>> findAllBooks() {
		return ResponseEntity.ok(bookService.findAllBooks());
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<DtoBook>findBookById(@PathVariable Long id) {
		return ResponseEntity.ok(bookService.findBookById(id));
	}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
	@PostMapping("/save")
	public ResponseEntity<DtoBook> saveBook(@Valid @RequestBody DtoBookIU dtoBookIU) {
	    return ResponseEntity.ok(bookService.saveBook(dtoBookIU));
	}

}




