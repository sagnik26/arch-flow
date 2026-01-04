package com.archflow.archigen.service;

import com.archflow.archigen.api.request.TrendingLinksRequest;
import com.archflow.archigen.api.response.TrendingLinksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingLinksService {
    private final ClaudeAIFetchLinksService claudeAIService;

    public TrendingLinksResponse getTrendingLinks(TrendingLinksRequest request) {
        try {
            log.info("topic: {} type: {} ", request.getTopic(), request.getType());
            TrendingLinksResponse response = claudeAIService.fetchTrendingSystemDesignLinks(request.getTopic(), request.getType());

            log.info("Received {} links from Claude", response.getLinks().size());
            return response;
        }
        catch (IllegalArgumentException e) {
            log.error("❌ Validation error: {}", e.getMessage());
            throw e;
        }
        catch (Exception ex) {
            log.error("❌ Claude API Failed {} ", request.getTopic(), ex);
            throw new RuntimeException("Failed to fetch trending links: " + ex.getMessage());
        }
    }
}
