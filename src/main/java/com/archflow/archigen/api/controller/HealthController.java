package com.archflow.archigen.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint
 * <p>
 * Used to verify the service is running
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Health check endpoint
     *
     * @return Status information
     * Example response:
     * {
     *   "status": "UP",
     *   "service": "ArchIGen",
     *   "version": "0.1.0",
     *   "timestamp": "2024-01-03T10:30:00"
     * }
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ArchIGen");
        response.put("version", "0.1.0");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
