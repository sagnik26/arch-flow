package com.archflow.archigen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Format for diagram data extracted by Claude
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramData {
    private String title;
    private String type;
    private String description;
    private List<Component> components;
    private List<Relationship> relationships;
    private List<Layer> layers;
}
