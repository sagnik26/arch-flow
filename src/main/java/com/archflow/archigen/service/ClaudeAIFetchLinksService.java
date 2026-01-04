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
                ? "High Level Design (architecture, components, data flow)"
                : "Low Level Design (detailed implementation, classes, algorithms)";

        return String.format("""
                You are a system design expert. Find the top 10 most relevant and authoritative 
                web links about "%s" for %s.
                
                Focus on:
                - Official documentation (AWS, GCP, Azure, etc.)
                - System design articles from tech blogs
                - Educational resources (GeeksforGeeks, System Design Primer)
                - GitHub repositories with good documentation
                
                Return ONLY valid JSON in this exact format (no markdown, no code blocks):
                
                {
                  "links": [
                    {
                      "title": "Example Title",
                      "url": "https://example.com",
                      "source": "Source Name"
                    }
                  ]
                }
                
                CRITICAL: Return ONLY the raw JSON. No ```json wrapper, no explanations.
                """, topic, typeDesc);
    }
}
