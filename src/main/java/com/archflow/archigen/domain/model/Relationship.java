package com.archflow.archigen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a relationship/connection between two components
 * <p>
 * Example: LoadBalancer → AppServer (labeled "routes traffic")
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {
    private String from;

    private  String to;

    private String label;
}
