package com.archflow.archigen.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Load .env file BEFORE Spring creates ANY beans
 * This ensures CLAUDE_API_KEY is available when ClaudeConfig initializes
 */
@Slf4j
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        try {
            System.out.println("🔧 DotenvConfig: Starting to load .env file...");

            // Load .env file
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")              // Project root
                    .ignoreIfMissing()            // Don't crash if missing
                    .load();

            // Convert to Map for Spring
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                envMap.put(key, value);
                // Also set as system property
                System.setProperty(key, value);

                // Debug log (mask sensitive values)
                System.out.println("📝 Loaded: " + key + " = " + maskValue(value));
            });

            // Add to Spring Environment with HIGHEST priority
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenvProperties", envMap));

            System.out.println("✅ Loaded " + envMap.size() + " environment variables from .env file");

        } catch (Exception e) {
            System.err.println("❌ Failed to load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Mask sensitive values for logging
     */
    private String maskValue(String value) {
        if (value == null || value.length() < 10) {
            return "***";
        }
        return value.substring(0, 7) + "..." + value.substring(value.length() - 4);
    }
}
