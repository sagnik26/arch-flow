package com.archflow.archigen.domain.model;

import com.archflow.archigen.domain.enums.RelationshipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a relationship between components
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    private String from;
    private String to;
    private RelationshipType type;
    private String description;
    private String protocol;
}