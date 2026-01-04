package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a component with its position coordinates
 * <p>
 * Used for rendering the diagram
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateNode {
    private int x;

    private int y;

    private ComponentType type;
}
