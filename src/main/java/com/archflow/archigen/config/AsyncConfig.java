package com.archflow.archigen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Async configuration using Java 21 Virtual Threads
 *
 * Virtual Threads are perfect for I/O-bound operations like:
 * - Web scraping
 * - API calls to OpenAI
 * - File operations
 * <p>
 * Benefits:
 * - Lightweight (millions of threads possible)
 * - Better resource utilization
 * - No need for thread pools
 */

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Using Java 21's Virtual Threads for better concurrency
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
