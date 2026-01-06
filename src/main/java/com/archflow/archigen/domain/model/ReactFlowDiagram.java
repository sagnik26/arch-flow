package com.archflow.archigen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Complete diagram in React Flow format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactFlowDiagram {
    private String id;
    private String title;
    private String description;
    private String type;
    private String topic;
    private Instant createdAt;
    private List<DiagramNode> nodes;
    private List<DiagramEdge> edges;
    private LayoutConfig layout;
    private List<LayerInfo> layers;
    private Viewport viewport;
    private Map<String, Object> metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutConfig {
        private String algorithm;
        private String direction;
        private Integer nodeSpacing;
        private Integer levelSpacing;
        private Boolean autoLayout;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayerInfo {
        private String id;
        private String name;
        private String displayName;
        private Integer order;
        private String color;
        private Boolean visible;
        private Boolean expanded;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Viewport {
        private Double x;
        private Double y;
        private Double zoom;
    }
}
