package com.archflow.archigen.service;

import com.archflow.archigen.config.ClaudeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeAIService {
    private final ClaudeConfig claudeConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String callClaudeAPI(String prompt) {
        try {
            log.info("🤖 Calling Claude API for extracting components");

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

            String response = webClient.post()
                    .uri("https://api.anthropic.com/v1/messages")
                    .header("x-api-key", claudeConfig.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(180))
                    .block();

            log.info("✅ Received response from Claude API");
            log.debug("Raw response: {}", response);

            // Parse the API response
            JsonNode root = objectMapper.readTree(response);

            // Extract the actual text content from content[0].text
            JsonNode contentArray = root.path("content");
            if (contentArray.isArray() && !contentArray.isEmpty()) {
                JsonNode firstContent = contentArray.get(0);
                String text = firstContent.path("text").asText();

                log.info("✅ Extracted text from Claude response: {} chars", text.length());

                return text;  // 👈 Return ONLY the text, not the full API response!
            }

            throw new RuntimeException("No content in Claude API response");

        } catch (WebClientResponseException e) {
            log.error("❌ Claude API Error - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Claude API error: " + e.getResponseBodyAsString(), e);

        } catch (Exception ex) {
            log.error("❌ Error calling Claude API", ex);
            throw new RuntimeException("Failed to fetch links: " + ex.getMessage(), ex);
        }
    }
}
