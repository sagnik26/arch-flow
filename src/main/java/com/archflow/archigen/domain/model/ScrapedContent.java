package com.archflow.archigen.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents scraped content from a URL
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedContent {
    /**
     * Original URL
     */
    private String url;

    /**
     * Page title
     */
    private String title;

    /**
     * Main text content (cleaned)
     */
    private String content;

    /**
     * Raw HTML (optional)
     */
    private String rawHtml;

    /**
     * Success status
     */
    private boolean success;

    /**
     * Error message if failed
     */
    private String errorMessage;

    /**
     * Content length in characters
     */
    private int contentLength;

    /**
     * Time taken to scrape (ms)
     */
    private long scrapeDurationMs;

    /**
     * When it was scraped
     */
    private Instant scrapedAt;

    /**
     * Source website (domain)
     */
    private String source;
}
