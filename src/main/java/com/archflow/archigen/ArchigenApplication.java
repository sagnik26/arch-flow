package com.archflow.archigen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ArchigenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchigenApplication.class, args);
    }
}
