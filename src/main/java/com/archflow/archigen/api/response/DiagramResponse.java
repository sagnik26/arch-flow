package com.archflow.archigen.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramResponse {
    /**
     * Unique identifier for this diagram
     */
    private String diagramId;

    /**
     * SVG content (can be rendered directly in browser)
     */
    private String svgContent;

    /**
     * Original topic requested
     */
    private String topic;

    /**
     * Diagram type (HLD/LLD)
     */
    private String type;

    /**
     * Generation status: SUCCESS, FAILED, PROCESSING
     */
    private String status;
}
