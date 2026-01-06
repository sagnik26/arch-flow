# ArchFlow Backend - AI-Powered Architecture Diagram Generator

> Transform any software system topic into beautiful, production-ready architecture diagrams using AI-powered analysis and web scraping.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 🎯 Overview

ArchFlow Backend is a Spring Boot 4 application that automatically generates high-level and architecture diagrams from any software topic. It uses Claude AI to discover relevant URLs, scrape architectural content, extract components and relationships, and convert them into React-Flow compatible diagrams.

### ✨ Key Features

- 🤖 **AI-Powered Component Extraction** - Claude Sonnet 4.5 analyzes content and identifies architectural components
- 🕷️ **Intelligent Web Scraping** - Parallel scraping with 90% success rate (Virtual Threads)
- 📡 **Real-time Progress Streaming** - SSE support for live progress updates
- 🎨 **React Flow Ready** - Auto-positioned, styled diagrams ready for frontend rendering
- 📊 **Multiple Diagram Types** - HLD [support for LLD and more in progress]

---

## 🚀 Quick Start

### Prerequisites

- Java 21+ (for Virtual Threads)
- Maven 3.9+
- Claude API Key (Anthropic)

### Installation
```bash
# Clone the repository
[git clone https://github.com/your-org/arch-flow.git](https://github.com/sagnik26/arch-flow.git) 
cd arch-flow

# Set environment variable
export CLAUDE_API_KEY="your-anthropic-api-key"

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

---

## ⚙️ Configuration

### application.properties
```properties
# Server Configuration
server.port=8080
spring.application.name=ArchFlow

# Claude API Configuration
claude.api.url=https://api.anthropic.com
claude.api.key=${CLAUDE_API_KEY}
claude.api.model=claude-sonnet-4-20250514
claude.api.max-tokens=8192
claude.api.timeout-seconds=120

# Thread Pool Configuration
spring.threads.virtual.enabled=true

# CORS Configuration (adjust for production)
cors.allowed-origins=http://localhost:3000,http://localhost:5173

# Optimization Settings
diagram.content.max-sources=5
diagram.content.max-chars-per-source=10000
diagram.content.min-source-length=1000
```

### Environment Variables
```bash
# Required
CLAUDE_API_KEY=sk-ant-api03-...

# Optional
SERVER_PORT=8080
CORS_ORIGINS=http://localhost:3000
```

---

## 📡 API Endpoints

Generate Diagram (Traditional)

**Endpoint:** `POST /api/v1/diagram`

**Description:** Generates a complete diagram and returns it after processing (30-40s)

**Request:**
```json
{
  "topic": "Food delivery applications",
  "type": "HLD"
}
```

**Response:** (After 30s)
```json
{
  "id": "uuid",
  "title": "Food Delivery Application - High-Level Design",
  "nodes": [...],
  "edges": [...],
  "layers": [...],
  "metadata": {
    "componentCount": 15,
    "relationshipCount": 20,
    "optimizedCharsUsed": 50000
  }
}
```

**Status Codes:**
- `200 OK` - Success
- `400 Bad Request` - Invalid input
- `500 Internal Server Error` - Processing failed

---
                                     
### Performance Optimizations

#### 1. **Virtual Threads (Java 21)**
```java
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// Benefits:
- Handle 1000s of concurrent requests
- HTTP threads freed immediately (<5ms)
- Background processing in virtual threads
- Perfect for I/O-bound operations
```

#### 2. **Parallel Web Scraping**
```java
// Scrape 10 URLs in parallel
CompletableFuture.allOf(futures).join();

// Result: 2-3 seconds for all 10 URLs
```

---

## 📦 Project Structure
```
src/main/java/com/archflow/ArchFlow/
├── api/
│   ├── controller/
│   │   └── DiagramPipelineController.java    # REST endpoints
│   ├── request/
│   │   └── TrendingLinksRequest.java         # Request DTOs
│   └── response/
│       └── WebScrapingResponse.java          # Response DTOs
├── domain/
│   ├── model/
│   │   ├── DiagramData.java                  # Raw diagram data
│   │   ├── ReactFlowDiagram.java             # React Flow format
│   │   ├── DiagramNode.java                  # Node model
│   │   ├── DiagramEdge.java                  # Edge model
│   │   ├── Component.java                    # Component model
│   │   ├── Relationship.java                 # Relationship model
│   │   └── DiagramProgress.java              # SSE progress
│   └── enums/
│       ├── ComponentType.java                # 40+ component types
│       ├── RelationshipType.java             # 25+ relationship types
│       └── DiagramType.java                  # Diagram types
├── service/
│   ├── DiagramPipelineService.java           # Orchestrates pipeline
│   ├── ComponentExtractionService.java       # AI extraction
│   ├── ReactFlowConverterService.java        # Format conversion
│   ├── LayoutService.java                    # Auto-layout
│   ├── ClaudeAIService.java                  # Claude API client
│   ├── TrendingLinkService.java              # URL discovery
│   └── WebScrapingService.java               # Web scraping
└── config/
    ├── WebClientConfig.java                  # HTTP client config
    └── CorsConfig.java                       # CORS config
```

---

## 🎨 Component Types (40+)
```java
// Infrastructure
API_GATEWAY, LOAD_BALANCER, CDN, REVERSE_PROXY, SERVICE_MESH

// Services
MICROSERVICE, WEB_SERVICE, REST_API, GRAPHQL_API, GRPC_SERVICE

// Clients
MOBILE_APP, WEB_APP, DESKTOP_APP, CLI, IOT_DEVICE

// Data
DATABASE, NOSQL_DATABASE, CACHE, SEARCH_ENGINE, DATA_WAREHOUSE

// Messaging
MESSAGE_QUEUE, MESSAGE_BROKER, EVENT_BUS, STREAM_PROCESSOR

// Storage
OBJECT_STORAGE, FILE_STORAGE, BLOCK_STORAGE

// External
PAYMENT_GATEWAY, AUTH_PROVIDER, NOTIFICATION_SERVICE, ANALYTICS

// And more...
```

---

## 🔗 Relationship Types (25+)
```java
// Communication
CALLS, ROUTES_TO, BALANCES_TO, PROXIES_TO, SENDS_TO

// Data Operations
READS_FROM, WRITES_TO, QUERIES, STORES_IN, CACHES_IN

// Messaging
PUBLISHES_TO, SUBSCRIBES_FROM, SENDS_TO, RECEIVES_FROM

// Integration
INTEGRATES_WITH, DEPENDS_ON, USES, EXTENDS, IMPLEMENTS

// And more...
```

---

## 🐛 Troubleshooting

### Common Issues

#### 1. **Claude API Timeout**
```
Error: TimeoutException after 30 seconds
```
**Solution:** Increase timeout in application.properties
```properties
claude.api.timeout-seconds=180
```

#### 2. **Response Truncated (max_tokens)**
```
Error: stop_reason="max_tokens"
```
**Solution:** Increase max tokens
```properties
claude.api.max-tokens=8192
```

#### 3. **Too Many Concurrent Requests**
```
Error: 503 Service Unavailable
```
**Solution:** Virtual threads should handle this, but verify:
```properties
spring.threads.virtual.enabled=true
```

#### 4. **CORS Errors**
```
Error: CORS policy blocked
```
**Solution:** Add your frontend URL
```properties
cors.allowed-origins=http://localhost:3000
```

#### 5. **Jackson Parsing Errors (Spring Boot 4)**
```
Error: Cannot find class com.fasterxml.jackson.*
```
**Solution:** Use correct imports for Spring Boot 4
```java
import tools.jackson.databind.ObjectMapper;  // NOT com.fasterxml.jackson
```
---

## 🤝 Contributing

Contributions are always welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

**Made with ❤️ by the Sagnik Ghosh**
