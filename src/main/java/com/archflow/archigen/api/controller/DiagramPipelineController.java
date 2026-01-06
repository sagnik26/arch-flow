package com.archflow.archigen.api.controller;

import com.archflow.archigen.api.request.TrendingLinksRequest;
import com.archflow.archigen.api.response.WebScrapingResponse;
import com.archflow.archigen.domain.model.DiagramData;
import com.archflow.archigen.domain.model.ReactFlowDiagram;
import com.archflow.archigen.service.ComponentExtractionService;
import com.archflow.archigen.service.DiagramPipelineService;
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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class DiagramPipelineController {
    private final DiagramPipelineService diagramPipelineService;
    private final ComponentExtractionService componentExtractionService;
    private final ReactFlowConverterService reactFlowConverterService;
    private final LayoutService layoutService;

    /**
     * Diagram Pipeline
     * */
    @PostMapping("/diagram")
    public ResponseEntity<ReactFlowDiagram> genContent(
            @Valid @RequestBody TrendingLinksRequest request
            ) {
        log.info("Starting pipeline with topic: {} & type: {}",
                request.getTopic(), request.getType());

        WebScrapingResponse response = diagramPipelineService.getComponentRelationshipData(request);

        log.info("Response: {}",response);

        DiagramData data = componentExtractionService.extracDiagram(response, request.getTopic(), request.getType());

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

        metadata.put("sourceUrlCount", response.getTotalUrls());
        metadata.put("successfulScrapes", response.getSuccessCount());
        metadata.put("failedScrapes", response.getFailureCount());

        int totalContentChars = response.getScrapedContent().stream()
                .filter(c -> c.getContent() != null)
                .mapToInt(c -> c.getContent().length())
                .sum();
        metadata.put("totalContentChars", totalContentChars);

        metadata.put("componentCount", reactFlowData.getNodes().size());
        metadata.put("relationshipCount", reactFlowData.getEdges().size());
        metadata.put("layerCount", reactFlowData.getLayers().size());
        metadata.put("diagramType", request.getType().toString());
        metadata.put("generatedBy", "archigen-v1");
        metadata.put("modelUsed", "claude-sonnet-4-5");

        reactFlowData.setMetadata(metadata);

        return ResponseEntity.ok(reactFlowData);
    }
}
