package com.archflow.archigen.api.response;

import com.archflow.archigen.domain.model.ScrapedContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing scraped content
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebScrapingResponse {
    /**
     * Successfully scraped content
     */
    private List<ScrapedContent> scrapedContent;

    /**
     * Total URLs processed
     */
    private int totalUrls;

    /**
     * Successful scrapes
     */
    private int successCount;

    /**
     * Failed scrapes
     */
    private int failureCount;

    /**
     * Total time taken (ms)
     */
    private long totalDurationMs;
}
