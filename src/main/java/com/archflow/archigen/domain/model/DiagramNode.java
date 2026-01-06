package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * React Flow compatible node
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramNode {
    private String id;
    private ComponentType type;
    private Position position;
    private NodeData data;
    private NodeStyle style;
    private String layer;
    private Boolean draggable;
    private Boolean selectable;
    private Boolean deletable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        private Double x;
        private Double y;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeData {
        private String label;
        private String description;
        private String technology;
        private String icon;
        private Integer instances;
        private String version;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeStyle {
        private String backgroundColor;
        private String borderColor;
        private Integer borderWidth;
        private String borderStyle;
        private Integer width;
        private Integer height;
        private Integer borderRadius;
        private String color;
        private Integer fontSize;
        private String fontWeight;
    }
}
