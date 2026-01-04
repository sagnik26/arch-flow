package com.archflow.archigen.domain.model;

import java.util.List;
import java.util.Map;

/**
 * Complete layout information for a diagram
 * <p>
 * Contains:
 * - Node positions (where each component should be placed)
 * - Edge information (how components are connected)
 */
public class DiagramLayout {

    /**
     * Map of component name to its position and type
     */
    private Map<String, CoordinateNode> nodes;

    /**
     * List of connections between components
     */
    private List<Relationship> edges;
}
