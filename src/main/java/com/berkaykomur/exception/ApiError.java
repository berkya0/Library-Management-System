package com.berkaykomur.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ApiError {
    private final int status;
    private final String message;
    private final String error;
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String path;
    private final Map<String, String> validationErrors;

}