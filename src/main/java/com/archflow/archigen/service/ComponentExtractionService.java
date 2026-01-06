package com.archflow.archigen.service;

import com.archflow.archigen.api.response.WebScrapingResponse;
import com.archflow.archigen.domain.enums.DiagramType;
import com.archflow.archigen.domain.model.DiagramData;
import com.archflow.archigen.domain.model.ScrapedContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ComponentExtractionService {
    private final ObjectMapper objectMapper;
    private final ClaudeAIService claudeAIService;

    public DiagramData extracDiagram(
            WebScrapingResponse webScrapingResponse,
            String topic,
            DiagramType type
    ) {
        log.info("🧩 Extracting components for: {} ({})", topic, type);

        // Combine scraped content
        String combinedContent = combineScrapedContent(webScrapingResponse);

        if (combinedContent.isEmpty()) {
            throw new RuntimeException("No content available for extraction");
        }

        log.info("📄 Combined content size: {} chars", combinedContent.length());

        // Build prompt
        String prompt = buildComponentExtractionPrompt(topic, type, combinedContent);

        // Call Claude
        log.info("🤖 Calling Claude AI for component extraction...");
        String response = claudeAIService.callClaudeAPI(prompt);

        log.info("***DIAGRAM RESP*** {}", response);

        // Parse response
        DiagramData diagramData = parseDiagramData(response);

        log.info("✅ Extracted {} components and {} relationships",
                diagramData.getComponents().size(),
                diagramData.getRelationships() != null ? diagramData.getRelationships().size() : 0);

        return diagramData;
    }

    /**
     * Combine scraped contents
     */
    private String combineScrapedContent(WebScrapingResponse response) {
        return response.getScrapedContent().stream()
                .filter(ScrapedContent::isSuccess)
                .filter(c -> c.getContentLength() > 500)
                .map(c -> String.format("""
                        
                        ========================================
                        SOURCE: %s
                        URL: %s
                        ========================================
                        
                        %s
                        
                        """,
                        c.getTitle() != null ? c.getTitle() : "Untitled",
                        c.getUrl(),
                        c.getContent()))
                .collect(Collectors.joining("\n\n"));
    }

    /**
     * Build Claude Prompt for extracting components
     * */
    private String buildComponentExtractionPrompt(String topic, DiagramType type, String content) {
        String typeDescription = type == DiagramType.HLD
                ? "High-Level Design (HLD)"
                : "Low-Level Design (LLD)";

        return String.format("""
            You are an expert software architect. Analyze the following technical content about "%s" 
            and extract architectural components and their relationships to create a %s diagram.
            
            ═══════════════════════════════════════════════════════════════════
            AVAILABLE COMPONENT TYPES (use EXACTLY these strings):
            ═══════════════════════════════════════════════════════════════════
            
            DATA LAYER:
            DATABASE, NOSQL_DATABASE, CACHE, STORAGE, DATA_WAREHOUSE, SEARCH_ENGINE
            
            SERVICE LAYER:
            MICROSERVICE, SERVER, API_GATEWAY, LOAD_BALANCER, AUTHENTICATION_SERVICE,
            NOTIFICATION_SERVICE, PAYMENT_GATEWAY
            
            MESSAGING:
            MESSAGE_QUEUE, MESSAGE_BROKER, STREAM_PROCESSOR, EVENT_BUS
            
            INFRASTRUCTURE:
            CDN, DNS, FIREWALL, CONTAINER
            
            WORKERS:
            WORKER, SCHEDULER, BATCH_PROCESSOR
            
            EXTERNAL:
            EXTERNAL_API, MAP_SERVICE, LOCATION_SERVICE
            
            CLIENT:
            CLIENT, MOBILE_APP, WEB_APP, ADMIN_PANEL
            
            MONITORING:
            MONITORING, LOGGING, ANALYTICS
            
            AI/ML:
            ML_MODEL, ML_PIPELINE
            
            OTHER:
            OTHER
            
            ═══════════════════════════════════════════════════════════════════
            AVAILABLE RELATIONSHIP TYPES (use EXACTLY these strings):
            ═══════════════════════════════════════════════════════════════════
            
            COMMUNICATION: CALLS, ASYNC_CALL, PUBLISHES_TO, SUBSCRIBES_FROM, SENDS_TO
            DATA: READS_FROM, WRITES_TO, QUERIES, CACHES_IN, STORES_IN, REPLICATES_TO
            ROUTING: ROUTES_TO, BALANCES_TO, PROXIES_TO, FORWARDS_TO
            DEPENDENCIES: DEPENDS_ON, INTEGRATES_WITH, AUTHENTICATES_WITH, AUTHORIZES_VIA
            NOTIFICATIONS: NOTIFIES, TRIGGERS
            OTHER: CONNECTS_TO, MONITORS, LOGS_TO
            
            ═══════════════════════════════════════════════════════════════════
            LAYER ASSIGNMENT:
            ═══════════════════════════════════════════════════════════════════
            
            client, api, infrastructure, service, messaging, data, external
            
            ═══════════════════════════════════════════════════════════════════
            INSTRUCTIONS:
            ═══════════════════════════════════════════════════════════════════
            
            1. Identify 8-15 key components
            2. Use kebab-case IDs (e.g., "user-service", "order-db")
            3. Assign correct ComponentType from list above
            4. Suggest specific technologies (PostgreSQL, Spring Boot, etc.)
            5. Define 15-25 relationships (not more)
            6. Assign each component to a layer
            
            ═══════════════════════════════════════════════════════════════════
            OUTPUT FORMAT:
            ═══════════════════════════════════════════════════════════════════
            
            Return ONLY valid JSON. NO markdown, NO ```json wrapper, NO explanations.
            
            {
              "title": "%s - %s",
              "type": "%s",
              "description": "Brief description",
              "components": [
                {
                  "id": "kebab-case-id",
                  "name": "Display Name",
                  "type": "COMPONENT_TYPE",
                  "description": "What it does",
                  "technology": "Tech stack",
                  "layer": "layer_name"
                }
              ],
              "relationships": [
                {
                  "from": "source-id",
                  "to": "target-id",
                  "type": "RELATIONSHIP_TYPE",
                  "description": "What happens",
                  "protocol": "HTTPS"
                }
              ],
              "layers": [
                {
                  "name": "client",
                  "displayName": "Client Layer",
                  "order": 1
                }
              ]
            }
            
            ═══════════════════════════════════════════════════════════════════
            CONTENT TO ANALYZE:
            ═══════════════════════════════════════════════════════════════════
            
            %s
            """,
                topic,
                typeDescription,
                topic,
                typeDescription,
                type.name(),
                content
        );
    }

    private DiagramData parseDiagramData(String jsonResponse) {
        try {
            String cleanJson = cleanJsonResponse(jsonResponse);
            JsonNode root = objectMapper.readTree(cleanJson);
            DiagramData diagramData = objectMapper.treeToValue(root, DiagramData.class);
            validateDiagramData(diagramData);
            return diagramData;

        } catch (Exception e) {
            log.error("❌ Failed to parse diagram data: {}", e.getMessage());
            throw new RuntimeException("Failed to parse component extraction response", e);
        }
    }

    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }

    private void validateDiagramData(DiagramData diagramData) {
        if (diagramData.getComponents() == null || diagramData.getComponents().isEmpty()) {
            throw new RuntimeException("No components extracted");
        }
        log.info("✅ Validation passed: {} components, {} relationships",
                diagramData.getComponents().size(),
                diagramData.getRelationships() != null ? diagramData.getRelationships().size() : 0);
    }
}
