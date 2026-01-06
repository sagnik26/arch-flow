package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single architectural component
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Component {
    private String id;
    private String name;
    private ComponentType type;
    private String description;
    private String technology;
    private String layer;
}