package com.archflow.archigen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scraping")
@Data
public class ScrapingConfig {
    /**
     * Timeout for each HTTP request (seconds)
     */
    private int requestTimeoutSeconds = 10;

    /**
     * Max concurrent scraping tasks
     */
    private int maxConcurrentRequests = 5;

    /**
     * Delay between requests to same domain (ms)
     */
    private int delayBetweenRequestsMs = 1000;

    /**
     * Max content length to extract (characters)
     */
    private int maxContentLength = 50000;

    /**
     * User-Agent string for requests
     */
    private String userAgent = "ArchIGen-Bot/1.0 (Educational System Design Tool)";

    /**
     * Whether to respect robots.txt
     */
    private boolean respectRobotsTxt = true;

    /**
     * Rate limit: max requests per second (across all domains)
     */
    private double maxRequestsPerSecond = 5.0;
}
