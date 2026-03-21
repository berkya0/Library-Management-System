package com.berkaykomur.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/api/health")
    @Operation(summary = "Sistem sağlık kontrolü", description = "Uygulamanın aktif ve istek almaya hazır olup olmadığını kontrol eder.")
    public String health() {
        return "UP";
    }
}
