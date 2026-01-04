package com.archflow.archigen.api.controller;

import com.archflow.archigen.api.request.TrendingLinksRequest;
import com.archflow.archigen.api.response.TrendingLinksResponse;
import com.archflow.archigen.service.TrendingLinksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TrendingLinksController {

    private final TrendingLinksService trendingLinksService;

    @PostMapping("/trending-links")
    public ResponseEntity<TrendingLinksResponse> getTrendingLinks(
            @Valid @RequestBody TrendingLinksRequest request) {

        log.info("📊 Received diagram generation request - Topic: '{}', Type: {}",
                request.getTopic(), request.getType());

        TrendingLinksResponse response = trendingLinksService.getTrendingLinks(request);

        log.info("✅ Trending Links Generated, Count: {}", response.getLinks().size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Test Endpoint called");
        return ResponseEntity.ok("Diagram API is working!");
    }
}
