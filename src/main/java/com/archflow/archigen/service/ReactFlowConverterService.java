package com.archflow.archigen.service;

import com.archflow.archigen.domain.enums.ComponentType;
import com.archflow.archigen.domain.enums.RelationshipType;
import com.archflow.archigen.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts DiagramData to React Flow format
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReactFlowConverterService {

    public ReactFlowDiagram convertToReactFlow(DiagramData diagramData) {
        log.info("🔄 Converting {} components to React Flow format",
                diagramData.getComponents().size());

        List<DiagramNode> nodes = diagramData.getComponents().stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        List<DiagramEdge> edges = diagramData.getRelationships().stream()
                .map(this::convertToEdge)
                .collect(Collectors.toList());

        List<ReactFlowDiagram.LayerInfo> layers = convertLayers(diagramData.getLayers());

        return ReactFlowDiagram.builder()
                .title(diagramData.getTitle())
                .description(diagramData.getDescription())
                .type(diagramData.getType())
                .topic(diagramData.getTitle())
                .nodes(nodes)
                .edges(edges)
                .layers(layers)
                .layout(createDefaultLayout())
                .viewport(createDefaultViewport())
                .build();
    }

    private DiagramNode convertToNode(Component component) {
        ComponentType type = component.getType();

        return DiagramNode.builder()
                .id(component.getId())
                .type(type)
                .position(DiagramNode.Position.builder().x(0.0).y(0.0).build())
                .data(DiagramNode.NodeData.builder()
                        .label(component.getName())
                        .description(component.getDescription())
                        .technology(component.getTechnology())
                        .status("running")
                        .build())
                .style(DiagramNode.NodeStyle.builder()
                        .backgroundColor(type.getColor())
                        .borderColor(darkenColor(type.getColor()))
                        .borderWidth(2)
                        .borderStyle("solid")
                        .width(type.getDefaultWidth())
                        .height(type.getDefaultHeight())
                        .borderRadius(8)
                        .color("#FFFFFF")
                        .fontSize(14)
                        .fontWeight("500")
                        .build())
                .layer(component.getLayer())
                .draggable(true)
                .selectable(true)
                .deletable(true)
                .build();
    }

    private DiagramEdge convertToEdge(Relationship relationship) {
        RelationshipType type = relationship.getType();
        String edgeId = String.format("%s-%s-%s",
                relationship.getFrom(),
                relationship.getTo(),
                type.name().toLowerCase());

        return DiagramEdge.builder()
                .id(edgeId)
                .source(relationship.getFrom())
                .target(relationship.getTo())
                .type(type)
                .data(DiagramEdge.EdgeData.builder()
                        .label(relationship.getDescription())
                        .protocol(relationship.getProtocol())
                        .dataFlow("unidirectional")
                        .build())
                .style(DiagramEdge.EdgeStyle.builder()
                        .stroke(type.getColor())
                        .strokeWidth(2)
                        .strokeDasharray(getDashArray(type.getLineStyle()))
                        .markerEnd("arrow")
                        .animated(type.isAnimated())
                        .build())
                .animated(type.isAnimated())
                .deletable(true)
                .selectable(true)
                .build();
    }

    private List<ReactFlowDiagram.LayerInfo> convertLayers(List<Layer> layers) {
        if (layers == null || layers.isEmpty()) {
            return createDefaultLayers();
        }
        return layers.stream()
                .map(layer -> ReactFlowDiagram.LayerInfo.builder()
                        .id(layer.getName())
                        .name(layer.getName())
                        .displayName(layer.getDisplayName())
                        .order(layer.getOrder())
                        .color(getLayerColor(layer.getOrder()))
                        .visible(true)
                        .expanded(true)
                        .build())
                .collect(Collectors.toList());
    }

    private ReactFlowDiagram.LayoutConfig createDefaultLayout() {
        return ReactFlowDiagram.LayoutConfig.builder()
                .algorithm("dagre")
                .direction("TB")
                .nodeSpacing(150)
                .levelSpacing(100)
                .autoLayout(true)
                .build();
    }

    private ReactFlowDiagram.Viewport createDefaultViewport() {
        return ReactFlowDiagram.Viewport.builder()
                .x(0.0).y(0.0).zoom(0.8)
                .build();
    }

    private List<ReactFlowDiagram.LayerInfo> createDefaultLayers() {
        List<ReactFlowDiagram.LayerInfo> layers = new ArrayList<>();
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("client").name("client").displayName("Client Layer")
                .order(1).color("#3498DB").visible(true).expanded(true).build());
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("api").name("api").displayName("API Layer")
                .order(2).color("#1ABC9C").visible(true).expanded(true).build());
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("service").name("service").displayName("Service Layer")
                .order(3).color("#2ECC71").visible(true).expanded(true).build());
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("messaging").name("messaging").displayName("Messaging Layer")
                .order(4).color("#F39C12").visible(true).expanded(true).build());
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("data").name("data").displayName("Data Layer")
                .order(5).color("#E74C3C").visible(true).expanded(true).build());
        layers.add(ReactFlowDiagram.LayerInfo.builder()
                .id("external").name("external").displayName("External Services")
                .order(6).color("#95A5A6").visible(true).expanded(true).build());
        return layers;
    }

    private String darkenColor(String hexColor) {
        return hexColor; // Simplified
    }

    private String getDashArray(String lineStyle) {
        return switch (lineStyle) {
            case "dashed" -> "5 5";
            case "dotted" -> "2 2";
            default -> "0";
        };
    }

    private String getLayerColor(int order) {
        String[] colors = {"#3498DB", "#1ABC9C", "#2ECC71", "#F39C12", "#E74C3C", "#95A5A6"};
        return colors[Math.min(order - 1, colors.length - 1)];
    }
}
