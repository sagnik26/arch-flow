package com.archflow.archigen.service;

import com.archflow.archigen.domain.model.DiagramNode;
import com.archflow.archigen.domain.model.ReactFlowDiagram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Applies automatic layout to diagram nodes
 */
@Service
@Slf4j
public class LayoutService {

    private static final int LAYER_SPACING = 200;
    private static final int NODE_SPACING = 180;

    public void applyLayout(ReactFlowDiagram diagram) {
        log.info("📐 Applying layout to {} nodes", diagram.getNodes().size());

        Map<String, List<DiagramNode>> nodesByLayer = diagram.getNodes().stream()
                .collect(Collectors.groupingBy(
                        node -> node.getLayer() != null ? node.getLayer() : "default"
                ));

        List<String> sortedLayers = diagram.getLayers().stream()
                .sorted(Comparator.comparingInt(ReactFlowDiagram.LayerInfo::getOrder))
                .map(ReactFlowDiagram.LayerInfo::getName)
                .collect(Collectors.toList());

        int currentY = 50;
        for (String layerName : sortedLayers) {
            List<DiagramNode> nodesInLayer = nodesByLayer.getOrDefault(layerName, List.of());
            if (!nodesInLayer.isEmpty()) {
                positionNodesInLayer(nodesInLayer, currentY);
                currentY += LAYER_SPACING;
            }
        }

        log.info("✅ Layout applied successfully");
    }

    private void positionNodesInLayer(List<DiagramNode> nodes, int y) {
        int totalWidth = nodes.size() * NODE_SPACING;
        int startX = -totalWidth / 2;

        for (int i = 0; i < nodes.size(); i++) {
            DiagramNode node = nodes.get(i);
            int x = startX + (i * NODE_SPACING);
            node.setPosition(DiagramNode.Position.builder()
                    .x((double) x).y((double) y).build());
        }
    }
}