package com.archflow.archigen.api.request;

import com.archflow.archigen.domain.enums.DiagramType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for diagram generation
 * <p>
 * Example JSON:
 * {
 *   "topic": "Netflix Architecture",
 *   "type": "HLD"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendingLinksRequest {

    /**
     * Example: "URL Shortener", "E-commerce System", "Netflix"
     */
    @NotBlank(message = "Topic is required")
    private String topic;

    /**
     * Example: "HLD", "LLD"
     */
    @NotNull(message = "Diagram type is required")
    private DiagramType type;
}
