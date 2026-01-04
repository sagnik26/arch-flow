package com.archflow.archigen.domain.enums;

/**
 * Types of components in system design
 * <p>
 * Each type will be mapped to a different shape/color in the diagram
 */
public enum ComponentType {
    DATABASE,         // Cylinder shape
    SERVER,           // Rectangle
    LOAD_BALANCER,    // Cloud/Diamond shape
    CACHE,            // Hexagon
    MESSAGE_QUEUE,    // Parallelogram
    API_GATEWAY,      // Trapezoid
    CDN,              // Cloud
    MICROSERVICE,     // Small rectangle
    CLIENT,           // Circle/Ellipse
    STORAGE,          // Cylinder (different color)
    NETWORK,          // Diamond
    MONITORING,       // Eye icon
    OTHER             // Default rectangle
}
