package com.archflow.archigen.service;

import com.archflow.archigen.api.response.TrendingLinksResponse;
import com.archflow.archigen.config.ClaudeConfig;
import com.archflow.archigen.domain.enums.DiagramType;
import tools.jackson.databind.JsonNode;      // ✅ CORRECT for Spring Boot 4
import tools.jackson.databind.ObjectMapper;  // ✅ CORRECT for Spring Boot 4
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeAIFetchLinksService {

    private final ClaudeConfig claudeConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrendingLinksResponse fetchTrendingSystemDesignLinks(String topic, DiagramType type) {
        try {
            log.info("🤖 Calling Claude API for topic: '{}'", topic);

            String prompt = buildPrompt(topic, type);

            Map<String, Object> request = Map.of(
                    "model", claudeConfig.getModel(),
                    "max_tokens", claudeConfig.getMaxTokens(),
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    )
            );

            log.info("Claude API KEY {}", claudeConfig.getApiKey());

            String response = webClient.post()
                    .uri("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", claudeConfig.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            log.info("✅ Received response from Claude API");
            log.debug("Raw response: {}", response);

            return parseResponse(response);

        } catch (WebClientResponseException e) {
            log.error("❌ Claude API Error - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Claude API error: " + e.getResponseBodyAsString(), e);

        } catch (Exception ex) {
            log.error("❌ Error calling Claude API", ex);
            throw new RuntimeException("Failed to fetch links: " + ex.getMessage(), ex);
        }
    }

    private TrendingLinksResponse parseResponse(String rawJson) {
        try {
            log.debug("🔍 Parsing Claude response...");

            JsonNode root = objectMapper.readTree(rawJson);

            String content = root
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();

            log.debug("📝 Extracted content: {}", content);
            String cleanedJson = stripMarkdownCodeBlocks(content);

            TrendingLinksResponse response = objectMapper.readValue(
                    cleanedJson,
                    TrendingLinksResponse.class
            );

            if (response.getLinks() == null || response.getLinks().isEmpty()) {
                log.warn("⚠️ No links found in Claude response");
            } else {
                log.info("📊 Successfully parsed {} links", response.getLinks().size());
            }

            return response;

        } catch (Exception e) {
            log.error("❌ Failed to parse Claude response");
            log.error("Raw JSON: {}", rawJson);
            log.error("Error: ", e);
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    /**
     * Strip Markdown code blocks from JSON
     * Removes: ```json ... ``` or ``` ... ```
     */
    private String stripMarkdownCodeBlocks(String text) {
        if (text == null) {
            return null;
        }

        // Remove ```json at the start
        String cleaned = text.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring("```json".length());
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring("```".length());
        }

        // Remove trailing ```
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        return cleaned.trim();
    }

    private String buildPrompt(String topic, DiagramType type) {
        String typeDesc = type == DiagramType.HLD
                ? "High Level Design (HLD)"
                : "Low Level Design (LLD)";

        return String.format("""
                You are a system design expert. Find the top 10 most relevant and authoritative\s
                       web links about "%s" for %s.
                
                ✅ PRIORITIZE these sources (proven to work, static HTML):
                        - microservices.io/patterns/* (TARGET: 7 URLs - has excellent, reliable content)
                        - raw.githubusercontent.com/* (TARGET: 2-3 URLs - use RAW URLs only, NOT blob)
                
                ✅ OPTIONAL sources (use sparingly, 0-1 URLs each):
                        - martinfowler.com/articles/*
                        - aws.amazon.com/architecture/*
                
                ❌ NEVER USE these sources:
                        - github.com/*/blob/* (always convert to raw.githubusercontent.com)
                        - geeksforgeeks.org (JavaScript-heavy, unreliable)
                        - medium.com (paywall)
                        - educative.io, udemy.com, coursera.org (paid)
                        - redis.io/docs/*, mongodb.com/blog/* (JavaScript-rendered)
                        - youtube.com (video, not text)
                        - Site homepages or landing pages
                
                CRITICAL REQUIREMENTS:
                        1. URLs must be REAL and VERIFIED - do not guess or make up URLs
                
                        2. For GitHub, ALWAYS use raw URLs:
                           ❌ BAD:  https://github.com/user/repo/blob/master/file.md
                           ✅ GOOD: https://raw.githubusercontent.com/user/repo/master/file.md
                
                        3. Target distribution:
                           - 7 URLs from microservices.io (proven 100%% success rate)
                           - 2 URLs from GitHub raw (specific markdown files)
                           - 1 URL from martinfowler.com or aws.amazon.com
                
                        4. Microservices.io has many excellent patterns:
                           - /patterns/data/event-sourcing.html
                           - /patterns/data/saga.html
                           - /patterns/data/cqrs.html
                           - /patterns/apigateway.html
                           - /patterns/data/database-per-service.html
                           - /patterns/reliability/circuit-breaker.html
                           - /patterns/data/api-composition.html
                           - /patterns/decomposition/decompose-by-subdomain.html
                
                        5. NO fragment URLs (no #anchors)
                
                        6. Link to specific articles/pages, NOT homepages
                
                        7. If unsure if a URL exists, don't include it
                
                        Return ONLY valid JSON (no markdown, no wrapper):
                        {
                          "links": [
                            {"title": "...", "url": "https://...", "source": "..."}
                          ]
                        }
                
                
                CRITICAL: Return ONLY the raw JSON. No ```json wrapper, no explanations.
                """, topic, typeDesc);
    }
}
