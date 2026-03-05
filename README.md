# Quarkus AI Agent — JavaLand 2026

Demo application for the talk **"Secure AI agents with Quarkus LangChain4j"**
at [JavaLand 2026](https://www.javaland.eu), Phantasialand Brühl — March 10, 2026.

A **JavaLand conference buddy** chatbot that knows the schedule, checks the weather,
and only answers with verified information — built with Quarkus, LangChain4j, RAG,
MCP, guardrails, and OIDC security.

## Architecture

```
Browser (WebSocket)
      │
      ▼
┌─────────────────────────────────────────┐
│         conference-buddy  :8080         │
│                                         │
│  ChatBot (LangChain4j AI Service)       │
│  ├── IPLookupClient     → ip-api.com    │
│  ├── @McpToolBox weather → :8081        │
│  ├── @McpToolBox javaland → :8082       │
│  ├── EasyRAG (javaland-guide.txt)       │
│  ├── MaxLength (input guardrail)        │
│  └── AllowedTalksGuardrail (output)     │
│                                         │
│  OIDC secured (Keycloak Dev Service)    │
└─────────────────────────────────────────┘
         │                    │
         ▼                    ▼
┌─────────────────┐  ┌─────────────────────┐
│ weather-mcp-    │  │ schedule-mcp-server  │
│ server  :8081   │  │ :8082                │
│                 │  │                      │
│ current_weather │  │ conference_schedule  │
│ → open-meteo    │  │ find_speaker         │
│                 │  │ find_talk_by_topic   │
│ OIDC secured    │  │ next_talks           │
└─────────────────┘  │                      │
                     │ OIDC secured         │
                     └─────────────────────┘
```

## Prerequisites

- Java 25+
- Maven 3.9+ (or use `./mvnw`)
- **One of**: [Ollama](https://ollama.com) with `llama3.2` pulled, OpenAI API key, or Google Gemini API key
- Docker (for Keycloak Dev Service, auto-started)

## Quick Start

Open **three terminals** and start each service in Quarkus dev mode:

```bash
# Terminal 1 — schedule MCP server (port 8082)
./mvnw quarkus:dev -pl schedule-mcp-server

# Terminal 2 — weather MCP server (port 8081)
./mvnw quarkus:dev -pl weather-mcp-server

# Terminal 3 — conference buddy chatbot (port 8080)
./mvnw quarkus:dev -pl conference-buddy
```

Then open http://localhost:8080 and log in as **alice** / **alice** (Keycloak Dev Service).

### Using OpenAI or Gemini instead of Ollama

Set environment variables and uncomment the relevant lines in `conference-buddy/src/main/resources/application.properties`:

```bash
# OpenAI
export OPENAI_API_KEY=sk-...

# Google Gemini (free tier available)
export GEMINI_API_KEY=AI...
```

## Try These Demo Questions

| Question | What it demonstrates |
|----------|----------------------|
| *"What's happening right now at JavaLand?"* | Schedule MCP tool |
| *"What should I see after this talk?"* | Schedule + RAG |
| *"Should I bring a jacket to ride Taron?"* | IP lookup → weather chain |
| *"Where should I grab coffee?"* | RAG (javaland-guide.txt) |
| *"Tell me about a restaurant called Le Café JavaLand"* | Output guardrail catches hallucination |
| *(paste 1001+ characters)* | Input guardrail blocks it |

## Project Structure

| Module | Port | Description |
|--------|------|-------------|
| `conference-buddy` | 8080 | Main chatbot app — WebSocket UI, LLM, RAG, guardrails |
| `weather-mcp-server` | 8081 | MCP server — current weather via open-meteo.com |
| `schedule-mcp-server` | 8082 | MCP server — JavaLand 2026 conference schedule |

## Running Tests

Guardrail unit tests run in milliseconds with **no LLM required**:

```bash
./mvnw test -pl conference-buddy
```

## Key Technologies

- **[Quarkus 3.32](https://quarkus.io)** — Cloud-native Java, dev mode, dev services
- **[Quarkus LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)** — AI services, tools, guardrails, RAG
- **[MCP (Model Context Protocol)](https://modelcontextprotocol.io)** — Secure tool sharing via SSE
- **[Keycloak](https://www.keycloak.org)** — OIDC authentication (auto-started via Dev Services)
- **[EasyRAG](https://docs.quarkiverse.io/quarkus-langchain4j/dev/easy-rag.html)** — Local ONNX embeddings, in-memory vector store
- **LLM providers** — Ollama (local), OpenAI, Google Gemini
