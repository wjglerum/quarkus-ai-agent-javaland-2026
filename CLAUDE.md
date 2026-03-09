# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Demo application for a JavaLand 2026 conference talk: "Secure AI agents with Quarkus LangChain4j". It implements a "Conference Buddy" chatbot that answers questions about the JavaLand 2026 conference (March 9-12, Europa-Park, Rust, Germany).

## Module Structure

Three independent Quarkus applications in a Maven multi-module project:

| Module | Port | Role |
|---|---|---|
| `conference-buddy` | 8080 | Main chatbot — WebSocket UI, AI service, RAG, MCP client |
| `weather-mcp-server` | 8081 | MCP server wrapping open-meteo.com REST API |
| `schedule-mcp-server` | 8082 | MCP server with hardcoded JavaLand 2026 schedule data |

Each module must be run independently. The MCP servers must be started before `conference-buddy`.

## Common Commands

Run in dev mode (from each module's directory):
```bash
cd conference-buddy && mvn quarkus:dev
cd weather-mcp-server && mvn quarkus:dev
cd schedule-mcp-server && mvn quarkus:dev
```

Run tests for a single module:
```bash
cd conference-buddy && mvn test
```

Run a specific test class:
```bash
cd conference-buddy && mvn test -Dtest=AllowedTalksGuardrailTest
```

Build all modules:
```bash
mvn install
```

## Architecture

### conference-buddy

- **`ChatBot.java`** — Core AI service interface. Annotated with `@RegisterAiService`, `@SessionScoped`. Declares the system prompt, input/output guardrails, and tool bindings (`@ToolBox` for local tools, `@McpToolBox` for MCP tools).
- **`ChatBotWebSocket.java`** — WebSocket endpoint handling user messages. Calls `ChatBot.chat()`.
- **Local tools**: `IPLookupClient.java` (REST client to ip-api.com), `CurrentTime.java` (returns current time).
- **Guardrails**: `MaxLength` (input, blocks messages over `guardrails.max-input-chars`), `AllowedTalksGuardrail` (output, rejects responses mentioning entities not in `guardrails.talks.allowed`).
- **RAG**: EasyRAG ingests `src/main/resources/rag/javaland-guide.txt` at startup using ONNX bge-small-en-q embeddings. No vector DB required.
- **Security**: Keycloak Dev Service (auto-started in dev mode via OIDC). Test credentials: `alice` / `alice`. OIDC tokens are propagated to MCP servers via `quarkus-langchain4j-oidc-mcp-auth-provider`.

### MCP Servers

Both use `streamable-http` transport (`/mcp` endpoint). The `conference-buddy` checks their health via MicroProfile Health (`/q/health`) instead of MCP ping to avoid authentication issues at startup.

- **`weather-mcp-server`**: Exposes `current_weather` tool. Calls open-meteo.com via a typed REST client.
- **`schedule-mcp-server`**: Exposes `conference_schedule`, `find_speaker`, `find_talk_by_topic`, `next_talks` tools. All schedule data is hardcoded in `JavaLandSchedule.java`.

## LLM Configuration

Active provider is set in `conference-buddy/src/main/resources/application.properties`. Currently configured for Google Gemini (requires `GEMINI_API_KEY` env var). Commented-out alternatives exist for OpenAI (requires `OPENAI_API_KEY`) and Ollama (local, no key needed).

To switch to Ollama locally, comment out the Gemini block and uncomment one of the Ollama blocks.

## Key Configuration

`guardrails.talks.allowed` in `application.properties` is a comma-separated allowlist of speaker names and venue/attraction names. The output guardrail blocks any AI response that mentions names not on this list. It must be kept in sync with `JavaLandSchedule.java` speaker data.
