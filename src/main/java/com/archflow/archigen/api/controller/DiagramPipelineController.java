package com.archflow.archigen.api.controller;

import com.archflow.archigen.api.request.TrendingLinksRequest;
import com.archflow.archigen.domain.model.DiagramData;
import com.archflow.archigen.domain.model.ReactFlowDiagram;
import com.archflow.archigen.service.ClaudeAIService;
import com.archflow.archigen.service.LayoutService;
import com.archflow.archigen.service.ReactFlowConverterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DiagramPipelineController {
    private final ClaudeAIService claudeAIService;
    private final ReactFlowConverterService reactFlowConverterService;
    private final LayoutService layoutService;
    private final ObjectMapper objectMapper;

    /**
     * Diagram Pipeline - Directly generates diagram JSON from AI in one call
     * */
    @PostMapping("/diagram")
    public ResponseEntity<ReactFlowDiagram> genContent(
            @Valid @RequestBody TrendingLinksRequest request
            ) {
        log.info("Starting pipeline with topic: {} & type: {}",
                request.getTopic(), request.getType());

        // Directly fetch diagram data from AI in one call
        DiagramData data = generateDiagramFromAI(request.getTopic(), request.getType());

        log.info("Extracted Diagram Data: {}", data);

        ReactFlowDiagram reactFlowData = reactFlowConverterService.convertToReactFlow(data);

        log.info("📐 Applying auto-layout...");
        layoutService.applyLayout(reactFlowData);

        // Set unique identifier
        reactFlowData.setId(UUID.randomUUID().toString());

        // Set timestamp
        reactFlowData.setCreatedAt(Instant.now());

        // Set topic from request
        reactFlowData.setTopic(request.getTopic());

        // set Metadata
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("componentCount", reactFlowData.getNodes().size());
        metadata.put("relationshipCount", reactFlowData.getEdges().size());
        metadata.put("layerCount", reactFlowData.getLayers().size());
        metadata.put("diagramType", request.getType().toString());
        metadata.put("generatedBy", "archigen-v2-direct");
        metadata.put("modelUsed", "claude-sonnet-4-5");
        metadata.put("generationMethod", "single-ai-call");

        reactFlowData.setMetadata(metadata);

        return ResponseEntity.ok(reactFlowData);
    }

    /**
     * Generate diagram data directly from AI in one call (no intermediate content generation)
     */
    private DiagramData generateDiagramFromAI(String topic, com.archflow.archigen.domain.enums.DiagramType type) {
        log.info("🤖 Generating diagram for '{}' using single AI call", topic);

        String typeDescription = type == com.archflow.archigen.domain.enums.DiagramType.HLD
                ? "High-Level Design"
                : "Low-Level Design";

        String prompt = String.format("""
            Create a %s diagram for "%s". Return ONLY valid JSON (no markdown, no explanations).

            COMPONENT TYPES: DATABASE, NOSQL_DATABASE, CACHE, STORAGE, MICROSERVICE, SERVER, API_GATEWAY,
            LOAD_BALANCER, AUTHENTICATION_SERVICE, MESSAGE_QUEUE, MESSAGE_BROKER, CDN, CLIENT, WEB_APP, MOBILE_APP

            RELATIONSHIP TYPES: CALLS, READS_FROM, WRITES_TO, ROUTES_TO, PUBLISHES_TO, SUBSCRIBES_FROM,
            AUTHENTICATES_WITH, CACHES_IN, BALANCES_TO

            LAYERS: client, api, service, messaging, data, infrastructure, external

            JSON FORMAT:
            {
              "title": "%s - %s",
              "type": "%s",
              "description": "Brief description",
              "components": [
                {"id": "kebab-case-id", "name": "Name", "type": "TYPE", "description": "What it does",
                 "technology": "Tech", "layer": "layer_name"}
              ],
              "relationships": [
                {"from": "id1", "to": "id2", "type": "TYPE", "description": "What happens", "protocol": "HTTPS"}
              ],
              "layers": [
                {"name": "client", "displayName": "Client Layer", "order": 1}
              ]
            }

            Include 5-10 components, 8-16 relationships. Use specific technologies.
            """,
                typeDescription,
                topic,
                topic,
                typeDescription,
                type.name()
        );

        String jsonResponse = claudeAIService.callClaudeAPI(prompt);
        log.info("✅ Received diagram JSON: {} chars", jsonResponse.length());

        return parseDiagramData(jsonResponse);
    }

    /**
     * Parse diagram JSON response
     */
    private DiagramData parseDiagramData(String jsonResponse) {
        try {
            String cleanJson = cleanJsonResponse(jsonResponse);
            JsonNode root = objectMapper.readTree(cleanJson);
            DiagramData diagramData = objectMapper.treeToValue(root, DiagramData.class);

            if (diagramData.getComponents() == null || diagramData.getComponents().isEmpty()) {
                throw new RuntimeException("No components extracted");
            }

            log.info("✅ Parsed {} components, {} relationships",
                    diagramData.getComponents().size(),
                    diagramData.getRelationships() != null ? diagramData.getRelationships().size() : 0);

            return diagramData;
        } catch (Exception e) {
            log.error("❌ Failed to parse diagram data: {}", e.getMessage());
            throw new RuntimeException("Failed to parse diagram response", e);
        }
    }

    /**
     * Clean JSON response (remove markdown wrappers)
     */
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        else if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }
}
