package com.archflow.archigen.domain.enums;

import lombok.Getter;

@Getter
public enum ComponentType {
    // Data Layer
    DATABASE("cylinder", "#4A90E2", 120, 80),
    NOSQL_DATABASE("cylinder", "#7B68EE", 120, 80),
    CACHE("hexagon", "#FF6B6B", 100, 100),
    STORAGE("cylinder", "#F39C12", 120, 80),
    DATA_WAREHOUSE("cylinder", "#8E44AD", 140, 80),
    SEARCH_ENGINE("hexagon", "#E74C3C", 120, 100),

    // Service Layer
    MICROSERVICE("rectangle", "#3498DB", 140, 80),
    SERVER("rectangle", "#2ECC71", 140, 80),
    API_GATEWAY("trapezoid", "#1ABC9C", 160, 80),
    LOAD_BALANCER("diamond", "#F1C40F", 120, 120),
    AUTHENTICATION_SERVICE("rectangle", "#E67E22", 160, 80),
    NOTIFICATION_SERVICE("rectangle", "#9B59B6", 160, 80),
    PAYMENT_GATEWAY("rectangle", "#16A085", 140, 80),

    // Messaging & Streaming
    MESSAGE_QUEUE("parallelogram", "#34495E", 140, 80),
    MESSAGE_BROKER("parallelogram", "#2C3E50", 160, 80),
    STREAM_PROCESSOR("parallelogram", "#7F8C8D", 160, 80),
    EVENT_BUS("parallelogram", "#95A5A6", 140, 80),

    // Infrastructure
    CDN("cloud", "#3498DB", 140, 100),
    DNS("diamond", "#1ABC9C", 100, 100),
    FIREWALL("shield", "#E74C3C", 100, 100),
    VPN("shield", "#F39C12", 100, 100),
    CONTAINER("rectangle", "#2980B9", 120, 80),

    // Workers & Processing
    WORKER("rectangle", "#8E44AD", 120, 70),
    SCHEDULER("rectangle", "#C0392B", 120, 70),
    BATCH_PROCESSOR("rectangle", "#27AE60", 140, 70),

    // External & Integration
    EXTERNAL_API("rectangle-dashed", "#95A5A6", 140, 80),
    WEBHOOK("rectangle-dashed", "#7F8C8D", 120, 70),
    MAP_SERVICE("rectangle-dashed", "#16A085", 140, 80),
    LOCATION_SERVICE("rectangle-dashed", "#D35400", 140, 80),

    // Client Layer
    CLIENT("ellipse", "#3498DB", 100, 100),
    MOBILE_APP("phone", "#1ABC9C", 80, 120),
    WEB_APP("monitor", "#2ECC71", 120, 100),
    ADMIN_PANEL("monitor", "#E67E22", 120, 100),
    IOT_DEVICE("circle", "#9B59B6", 80, 80),

    // Monitoring & Operations
    MONITORING("eye", "#E74C3C", 100, 80),
    LOGGING("file", "#F39C12", 100, 80),
    ALERTING("bell", "#E67E22", 100, 80),
    ANALYTICS("chart", "#8E44AD", 120, 80),

    // AI/ML
    ML_MODEL("rectangle", "#9B59B6", 140, 80),
    ML_PIPELINE("rectangle", "#8E44AD", 160, 80),

    // Other
    NETWORK("diamond", "#BDC3C7", 100, 100),
    OTHER("rectangle", "#95A5A6", 120, 80);

    private final String shape;
    private final String color;
    private final int defaultWidth;
    private final int defaultHeight;

    ComponentType(String shape, String color, int defaultWidth, int defaultHeight) {
        this.shape = shape;
        this.color = color;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
    }
}
