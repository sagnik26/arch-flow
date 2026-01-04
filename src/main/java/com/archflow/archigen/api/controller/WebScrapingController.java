package com.archflow.archigen.api.controller;

import com.archflow.archigen.api.response.TrendingLinksResponse;
import com.archflow.archigen.api.response.WebScrapingResponse;
import com.archflow.archigen.domain.model.LinkItem;
import com.archflow.archigen.service.WebScrapingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraping")
@RequiredArgsConstructor
@Slf4j
public class WebScrapingController {
    private final WebScrapingService webScrapingService;

    /**
     * Scrape Multiple Url
     * */
    @PostMapping("/scrape-multiple-urls")
    public ResponseEntity<WebScrapingResponse> scrapeLinks(
            @Valid @RequestBody TrendingLinksResponse trendingLinks) {

        log.info("🕷️ Received request to scrape {} links",
                trendingLinks.getLinks().size());

        WebScrapingResponse response = webScrapingService.scrapeUrls(trendingLinks.getLinks());

        log.info("✅ Scraping completed: {}/{} successful",
                response.getSuccessCount(),
                response.getTotalUrls());

        return ResponseEntity.ok(response);
    }

    /**
     * Scrape Single Url
     * */
    @GetMapping("/scrape-url")
    public ResponseEntity<WebScrapingResponse> scrapeSingleUrl(
            @RequestParam String url,
            @RequestParam(defaultValue = "Manual") String source) {

        log.info("🕷️ Scraping single URL: {}", url);

        List<LinkItem> links = List.of(
                LinkItem.builder()
                        .url(url)
                        .title("Single URL Test")
                        .source(source)
                        .build()
        );

        WebScrapingResponse response = webScrapingService.scrapeUrls(links);

        return ResponseEntity.ok(response);
    }

    /**
     * Scrape URL Health Check
     * */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Web scraping service is healthy! ✅");
    }

}
