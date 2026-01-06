package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * React Flow compatible edge
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramEdge {
    private String id;
    private String source;
    private String target;
    private RelationshipType type;
    private EdgeData data;
    private EdgeStyle style;
    private String sourceHandle;
    private String targetHandle;
    private Boolean animated;
    private Boolean deletable;
    private Boolean selectable;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EdgeData {
        private String label;
        private String description;
        private String protocol;
        private String dataFlow;
        private Integer latency;
        private String bandwidth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EdgeStyle {
        private String stroke;
        private Integer strokeWidth;
        private String strokeDasharray;
        private String markerEnd;
        private Boolean animated;
    }
}
