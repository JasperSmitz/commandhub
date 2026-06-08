# CommandHub

CommandHub is a configurable tool execution server built with Java and Spring Boot.

It exposes command-line tools through a simple HTTP API and an MCP stdio adapter. Tools are defined in an external `tools.json` file, so new tools can be added without changing or rebuilding the application.

This project was built as a learning exercise around Java, Spring Boot, backend architecture, external process execution, and the Model Context Protocol.

## Features

* Config-driven tool registry
* HTTP API for tool discovery and execution
* MCP stdio adapter
* External process execution with `ProcessBuilder`
* Structured stdout, stderr, exit code, duration, and timeout responses
* Request validation
* Tool configuration validation
* Safe manual reload of `tools.json`
* Structured error responses
* Logging
* Unit and integration tests

## Requirements

* Java 21
* Maven Wrapper, included in the project
* Any external commands used by configured tools must be available on the system PATH

## Running the HTTP server

```powershell
.\mvnw.cmd spring-boot:run
```

The server starts at:

```text
http://localhost:8080
```

## HTTP API

### Health check

```http
GET /health
```

### List tools

```http
GET /tools
```

### Execute a tool

```http
POST /tools/{toolName}
```

### Reload tools

```http
POST /admin/reload-tools
```

This reloads `tools.json` from disk without restarting the HTTP server. If the new configuration is invalid, the previous valid tool registry remains active.

## Running in MCP stdio mode

Build the jar:

```powershell
.\mvnw.cmd package
```

Run in MCP stdio mode:

```powershell
java -jar target\commandhub-0.0.1-SNAPSHOT.jar --mcp-stdio
```

In this mode, CommandHub reads JSON-RPC messages from stdin and writes JSON-RPC responses to stdout.

Supported MCP-style methods:

```text
initialize
tools/list
tools/call
resources/list
resources/read
```

## Tool configuration

Tools are defined in the root-level `tools.json` file.

Example:

```json
[
  {
    "name": "echo",
    "description": "Echo a message",
    "command": "cmd.exe",
    "arguments": [
      "/c",
      "echo",
      "{message}"
    ],
    "parameters": [
      {
        "name": "message",
        "type": "string",
        "required": true
      }
    ],
    "timeout": 5000
  }
]
```

## Running tests

```powershell
.\mvnw.cmd test
```

## Documentation

More detailed technical documentation can be found in:

```text
docs/TECHNICAL.md
```

## Status

CommandHub is a working proof-of-concept. It demonstrates how local commands can be exposed as configurable HTTP and MCP-accessible tools.
