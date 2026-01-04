package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a component in the system design
 * <p>
 * Example: LoadBalancer, Database, Redis Cache
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Component {
    private String name;

    private ComponentType type;

    private String description;
}
