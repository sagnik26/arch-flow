package com.archflow.archigen.domain.enums;
import lombok.Getter;

@Getter
public enum RelationshipType {
    // Communication
    CALLS("solid", "arrow", "#2C3E50", false),
    ASYNC_CALL("dashed", "arrow", "#7F8C8D", false),
    PUBLISHES_TO("solid", "arrow-circle", "#E74C3C", true),
    SUBSCRIBES_FROM("dashed", "arrow-circle", "#3498DB", true),
    SENDS_TO("solid", "arrow", "#2C3E50", false),

    // Data Operations
    READS_FROM("dashed", "arrow", "#3498DB", false),
    WRITES_TO("solid", "arrow", "#E74C3C", false),
    QUERIES("dashed", "arrow", "#9B59B6", false),
    CACHES_IN("dotted", "arrow", "#F39C12", false),
    STORES_IN("solid", "arrow", "#16A085", false),
    REPLICATES_TO("double", "arrow", "#8E44AD", false),

    // Routing & Traffic
    ROUTES_TO("solid", "arrow", "#1ABC9C", false),
    BALANCES_TO("solid", "arrow-multiple", "#F1C40F", false),
    PROXIES_TO("solid", "arrow", "#2ECC71", false),
    FORWARDS_TO("solid", "arrow", "#34495E", false),

    // Dependencies
    DEPENDS_ON("dashed", "arrow", "#7F8C8D", false),
    INTEGRATES_WITH("dotted", "arrow", "#95A5A6", false),
    AUTHENTICATES_WITH("solid", "lock", "#E67E22", false),
    AUTHORIZES_VIA("solid", "lock", "#C0392B", false),

    // Notifications
    NOTIFIES("solid", "bell", "#9B59B6", true),
    TRIGGERS("solid", "arrow", "#E74C3C", true),

    // Other
    CONNECTS_TO("solid", "arrow", "#2C3E50", false),
    MONITORS("dotted", "eye", "#3498DB", false),
    LOGS_TO("dashed", "arrow", "#F39C12", false);

    private final String lineStyle;
    private final String arrowType;
    private final String color;
    private final boolean animated;

    RelationshipType(String lineStyle, String arrowType, String color, boolean animated) {
        this.lineStyle = lineStyle;
        this.arrowType = arrowType;
        this.color = color;
        this.animated = animated;
    }
}
