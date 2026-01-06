package com.archflow.archigen.service;

import com.archflow.archigen.api.request.TrendingLinksRequest;
import com.archflow.archigen.api.response.TrendingLinksResponse;
import com.archflow.archigen.api.response.WebScrapingResponse;
import com.archflow.archigen.domain.model.ScrapedContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiagramPipelineService {
    private final TrendingLinksService trendingLinksService;
    private final WebScrapingService webScrapingService;
    private ScrapedContent content;

    public WebScrapingResponse getComponentRelationshipData(TrendingLinksRequest request) {
        TrendingLinksResponse response = trendingLinksService.getTrendingLinks(request);

        log.info("Trending links: {}", response.getLinks());

        return webScrapingService.scrapeUrls(response.getLinks());
    }
}
