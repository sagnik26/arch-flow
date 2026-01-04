package com.archflow.archigen.service;

import com.archflow.archigen.api.response.WebScrapingResponse;
import com.archflow.archigen.config.ScrapingConfig;
import com.archflow.archigen.domain.model.LinkItem;
import com.archflow.archigen.domain.model.ScrapedContent;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Service for scraping web content from URLs
 * Uses WebClient (Spring's reactive HTTP client) with Virtual Thread executor
 * Implements rate limiting and respectful scraping practices
 */
@Service
@Slf4j
public class WebScrapingService {
    private final ScrapingConfig scrapingConfig;
    private final  ContentExtractorService contentExtractor;
    private final WebClient webClient;
    private final Executor taskExecutor;
    private final RateLimiter rateLimiter;


    public WebScrapingService(
            ScrapingConfig scrapingConfig,
            ContentExtractorService contentExtractor,
            WebClient webClient,
            @Qualifier("taskExecutor") Executor taskExecutor
    ) {
        this.scrapingConfig = scrapingConfig;
        this.contentExtractor = contentExtractor;
        this.webClient = webClient;
        this.taskExecutor = taskExecutor;

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod((int) scrapingConfig.getMaxRequestsPerSecond())
                .timeoutDuration(Duration.ofSeconds(10))
                .build();

        this.rateLimiter = RateLimiter.of("webscraping", config);

        log.info("✅ WebScrapingService initialized with Resilience4j RateLimiter " +
                        "(limit: {} requests/second)",
                scrapingConfig.getMaxRequestsPerSecond());
    }

    public WebScrapingResponse scrapeUrls(List<LinkItem> urls) {
        log.info("🕷️ Starting to scrape {} URLs", urls.size());

        Instant startTime = Instant.now();

        List<CompletableFuture<ScrapedContent>> futures = urls.stream()
                .map(linkItem -> CompletableFuture.supplyAsync(
                        () -> scrapedUrl(linkItem),
                        taskExecutor
                )).toList();

        List<ScrapedContent> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        long totalDuration = Duration.between(startTime, Instant.now()).toMillis();

        int successCount = (int) results.stream().filter(ScrapedContent::isSuccess).count();
        int failureCount = results.size() - successCount;

        log.info("✅ Scraping completed: {} success, {} failures in {}ms",
                successCount, failureCount, totalDuration);

        return WebScrapingResponse.builder()
                .scrapedContent(results)
                .totalUrls(urls.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .totalDurationMs(totalDuration)
                .build();
    }

    private ScrapedContent scrapedUrl(LinkItem linkItem) {
        String url = linkItem.getUrl();
        log.debug("Scraping {}", url);

        Instant startTime = Instant.now();

        try {
            RateLimiter.waitForPermission(rateLimiter);

            String html = webClient.get()
                    .uri(url)
                    .header("User-Agent", scrapingConfig.getUserAgent())
//                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
//                    .header("Accept-Language", "en-US,en;q=0.9")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(scrapingConfig.getRequestTimeoutSeconds()))
                    .block();

            if (html == null || html.isEmpty()) {
                log.warn("⚠️ Empty response for: {}", url);
                return buildFailedContent(url, linkItem.getSource(), "Empty response", startTime);
            }

            Document doc = Jsoup.parse(html, url);
            String title = doc.title();
            String mainContent = contentExtractor.extractMainContent(doc);

            // Validate extracted content
            if (mainContent == null || mainContent.trim().isEmpty()) {
                log.warn("⚠️ Content extraction failed for: {}", url);
                return buildFailedContent(url, linkItem.getSource(),
                        "Content extraction failed - no meaningful content found", startTime);
            }

            int mainContentLength = mainContent.trim().length();

            if (mainContentLength < 200) {
                log.warn("⚠️ Content too small ({} chars) for: {}", mainContentLength, url);
                // You can decide: mark as failed OR keep as success with warning
                // For now, let's mark as failed for very small content
                return buildFailedContent(url, linkItem.getSource(),
                        "Content too small (" + mainContentLength + " chars) - likely JavaScript-rendered site",
                        startTime);
            }

            if(mainContentLength > scrapingConfig.getMaxContentLength()) {
                mainContent = mainContent.substring(0, scrapingConfig.getMaxContentLength())
                        + "... [TRUNCATED]";
            }

            long duration = Duration.between(startTime, Instant.now()).toMillis();
            log.info("✅ Scraped {} - {} chars in {}ms",
                    extractDomain(url), mainContent.length(), duration);

            return ScrapedContent.builder()
                    .success(true)
                    .url(url)
                    .title(title)
                    .content(mainContent)
                    .contentLength(mainContent.length())
                    .scrapeDurationMs(duration)
                    .scrapedAt(Instant.now())
                    .source(linkItem.getSource())
                    .build();

        }
        catch (WebClientResponseException e) {
            log.error("HTTP {} for {}: {}", e.getStatusCode(), url, e.getMessage());
            return buildFailedContent(url, linkItem.getSource(),
                    "HTTP " + e.getStatusCode(), startTime);
        }
        catch (Exception e) {
            log.error("Failed to scrape {}: {}", url, e.getMessage());
            return buildFailedContent(url, linkItem.getSource(), e.getMessage(), startTime);
        }
    }

    private ScrapedContent buildFailedContent(String url, String source, String errorMessage, Instant startTime) {
        long duration = Duration.between(startTime, Instant.now()).toMillis();

        return ScrapedContent.builder()
                .url(url)
                .success(false)
                .errorMessage(errorMessage)
                .scrapeDurationMs(duration)
                .scrapedAt(Instant.now())
                .source(source)
                .build();
    }

    private String extractDomain(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
