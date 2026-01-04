package com.archflow.archigen.api.response;

import com.archflow.archigen.domain.model.Component;
import com.archflow.archigen.domain.model.Relationship;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response from OpenAI containing extracted components and relationships
 * <p>
 * Example:
 * {
 *   "components": [
 *     {"name": "LoadBalancer", "type": "LOAD_BALANCER"},
 *     {"name": "AppServer", "type": "SERVER"}
 *   ],
 *   "relationships": [
 *     {"from": "LoadBalancer", "to": "AppServer", "label": "routes"}
 *   ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentsResponse {
    /**
     * List of components extracted from the topic
     */
    private List<Component> components;

    /**
     * List of relationships between components
     */
    private List<Relationship> relationships;
}
