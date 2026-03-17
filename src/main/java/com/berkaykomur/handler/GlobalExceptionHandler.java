package com.berkaykomur.handler;

import com.berkaykomur.exception.ApiError;
import com.berkaykomur.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = {BaseException.class})
	public ResponseEntity<ApiError> handleBaseException(BaseException ex , HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
		return ResponseEntity.status(ex.getStatus().value()).body(apiError);
	}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e,
                                                              HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiError apiError =ApiError.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .message("Validation error")
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}
