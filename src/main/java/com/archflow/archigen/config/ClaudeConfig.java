package com.archflow.archigen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI API configuration
 * <p>
 * Properties are loaded from application.yml
 * Can be overridden with environment variables
 */
@Configuration
@ConfigurationProperties(prefix = "claude")
@Data
public class ClaudeConfig {

    private String apiKey;

    private String model = "claude-sonnet-4-5";

    private Integer maxTokens = 1000;
}
