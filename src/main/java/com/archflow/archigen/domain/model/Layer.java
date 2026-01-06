package com.archflow.archigen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a layer in the architecture
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Layer {
    private String name;
    private String displayName;
    private Integer order;
}