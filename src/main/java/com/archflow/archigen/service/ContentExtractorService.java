package com.archflow.archigen.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for extracting main content from HTML pages
 * Removes ads, navigation, footers, and other noise
 * Focuses on article/blog content
 */
@Service
@Slf4j
public class ContentExtractorService {
    // Common selectors for main content
    private static final List<String> CONTENT_SELECTORS = Arrays.asList(
            "article",
            "[role='main']",
            ".post-content",
            ".article-content",
            ".entry-content",
            ".markdown-body",  // GitHub
            ".readme",         // GitHub
            "main",
            "#content",
            ".content"
    );

    // Elements to remove (noise)
    private static final List<String> NOISE_SELECTORS = Arrays.asList(
            "script",
            "style",
            "nav",
            "header",
            "footer",
            ".advertisement",
            ".ads",
            ".sidebar",
            ".comments",
            ".related-posts",
            "#comments",
            ".cookie-banner",
            ".popup",
            ".modal"
    );

    /**
     * Extract main content from HTML document
     * Strategy:
     * 1. Try common content selectors
     * 2. Remove noise (ads, navigation)
     * 3. Extract text
     * 4. Clean and format
     *
     * @param doc JSoup document
     * @return Cleaned main content text
     */
    public String extractMainContent(Document doc) {
        // Clone document to avoid modifying original
        Document workingDoc = doc.clone();

        // Remove noise elements
        removeNoiseElements(workingDoc);

        // Try to find main content area
        Element mainContent = findMainContentElement(workingDoc);

        if (mainContent != null) {
            log.debug("📄 Found main content using selector");
            return extractAndCleanText(mainContent);
        }

        // Fallback: use body
        log.debug("📄 Using body as fallback");
        return extractAndCleanText(workingDoc.body());
    }


    /**
     * Remove ads, navigation, and other noise
     */
    private void removeNoiseElements(Document doc) {
        for (String selector : NOISE_SELECTORS) {
            doc.select(selector).remove();
        }
    }

    /**
     * Find the main content element
     * */
    private Element findMainContentElement(Document doc) {
        for (String selector: CONTENT_SELECTORS) {
            Elements elements = doc.select(selector);
            if(!elements.isEmpty()) {
                return elements.stream()
                        .max(Comparator.comparingInt(e -> e.text().length()))
                        .orElse(null);
            }
        }
        return null;
    }

    /**
     * Extract and clean text from element
     * */
    private String extractAndCleanText(Element element) {
        // Get text with line breaks preserved
        String text = element.wholeText();

        // Clean HTML entities
        text = StringEscapeUtils.unescapeHtml4(text);

        // Remove excessive whitespace
        text = text.replaceAll("\\s+", " ");

        // Remove leading/trailing whitespace
        text = text.trim();

        // Add line breaks for paragraphs (helps readability for AI)
        Elements paragraphs = element.select("p, h1, h2, h3, h4, h5, h6, li");
        if (!paragraphs.isEmpty()) {
            text = paragraphs.stream()
                    .map(Element::text)
                    .filter(p -> !p.isBlank())
                    .collect(Collectors.joining("\n\n"));
        }

        return text;
    }
}
