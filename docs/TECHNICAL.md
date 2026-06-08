# CommandHub Technical Overview

This document explains the internal architecture and design decisions behind CommandHub.

## Concept

CommandHub is a generic tool execution layer.

Instead of hardcoding tools into the application, tools are defined as data in `tools.json`. The application loads these definitions, validates incoming requests, builds commands from configured arguments, executes external processes, and returns structured results.

The application itself does not need to know about specific tools at compile time.

## Architecture

```text
HTTP / MCP Adapter
        ↓
ToolExecutionService
        ↓
ToolRegistry
        ↓
ProcessExecutor
        ↓
External Command / Script
```

The core execution logic is shared between the HTTP API and MCP stdio adapter.

## Packages

```text
controller
```

Contains HTTP controllers and global HTTP exception handling.

```text
model
```

Contains records for tool definitions, requests, responses, and reload responses.

```text
config
```

Loads and validates `tools.json`.

```text
registry
```

Stores the currently active tool definitions in memory.

```text
validation
```

Validates incoming tool execution requests.

```text
service
```

Coordinates tool lookup, request validation, command building, process execution, and response creation.

```text
execution
```

Runs external commands using Java `ProcessBuilder`.

```text
mcp
```

Contains the stdio JSON-RPC adapter for MCP-style tool discovery and invocation.

## Tool Registry

`ToolRegistry` stores the currently active list of tools.

On startup, it:

1. Loads `tools.json`
2. Validates the configuration
3. Stores the tools in memory

The registry can also be manually reloaded through:

```http
POST /admin/reload-tools
```

Reloading is designed to be safe. A new config is loaded and validated before replacing the active registry. If validation fails, the old registry remains active.

## Tool Configuration

A tool definition contains:

| Field         | Description                            |
| ------------- | -------------------------------------- |
| `name`        | Unique tool name                       |
| `description` | Human-readable description             |
| `command`     | Executable command                     |
| `arguments`   | Command arguments                      |
| `parameters`  | Tool input parameters                  |
| `timeout`     | Maximum execution time in milliseconds |

Example:

```json
{
  "name": "git-status",
  "description": "Show the Git status of the current project",
  "command": "cmd.exe",
  "arguments": [
    "/c",
    "git",
    "status",
    "--short"
  ],
  "parameters": [],
  "timeout": 5000
}
```

## Parameters and Placeholders

Tool arguments may contain placeholders:

```json
"arguments": ["/c", "echo", "{message}"]
```

When the tool is called, placeholders are replaced with values from the request:

```json
{
  "parameters": {
    "message": "hello"
  }
}
```

A tool may have zero parameters:

```json
"parameters": []
```

This is valid for tools such as `java-version` or `git-status`.

## Request Validation

Incoming tool execution requests are validated before execution.

Validation checks:

* request body exists
* `parameters` field exists
* required parameters are present
* required parameters are not blank

Invalid requests return structured `400 Bad Request` responses.

## Configuration Validation

Tool configuration is validated on startup and reload.

Validation checks:

* tools list is not null
* tool names are present
* tool names are unique
* descriptions are present
* commands are present
* arguments are not null
* parameters are not null
* timeout is greater than zero
* parameter names are present
* parameter names are unique
* parameter types are present

This prevents broken configuration from silently entering the runtime registry.

## Process Execution

External commands are executed by `ProcessExecutor` using Java `ProcessBuilder`.

The executor captures:

* stdout
* stderr
* exit code
* duration
* timeout status

Example response:

```json
{
  "toolName": "git-status",
  "status": "success",
  "stdout": "M tools.json",
  "stderr": "",
  "exitCode": 0,
  "durationMs": 151,
  "timedOut": false
}
```

## HTTP API

The HTTP API exposes:

```http
GET /tools
```

Lists available tools.

```http
POST /tools/{toolName}
```

Executes a tool.

```http
POST /admin/reload-tools
```

Reloads `tools.json`.

## MCP stdio Adapter

CommandHub also supports an MCP-style stdio mode.

In this mode, the application does not start the HTTP server. Instead, it reads JSON-RPC messages from stdin and writes JSON-RPC responses to stdout.

Supported methods:

```text
initialize
tools/list
tools/call
resources/list
resources/read
```

### tools/list

Maps internal `ToolDefinition` objects to MCP-style tool descriptions.

Each tool includes:

* name
* description
* input schema

### tools/call

Receives a tool name and arguments, then delegates to `ToolExecutionService`.

### resources/list

Lists readable resources exposed by CommandHub.

Currently exposed resource:

```text
commandhub://tools/config
```

### resources/read

Reads the current `tools.json` file as a resource.

## Logging

The application logs important events such as:

* tools loaded on startup
* tool reload attempts
* successful reloads
* validation failures
* tool execution start and result
* process execution errors

In MCP stdio mode, normal application logging is disabled because stdout is reserved for JSON-RPC protocol messages.

## Error Handling

HTTP errors are returned in a structured format.

Example:

```json
{
  "error": "bad_request",
  "message": "Missing required parameter: message",
  "timestamp": "2026-06-01T09:59:44.356676500Z"
}
```

Unknown tools return `404 Not Found`.

Validation errors return `400 Bad Request`.

## Testing

The project includes:

* unit tests for request validation
* unit tests for configuration validation
* integration tests for HTTP controller behavior

Run all tests:

```powershell
.\mvnw.cmd test
```

## Security Considerations

CommandHub executes external processes. This must be treated carefully.

Current safety measures:

* only configured tools can be executed
* arbitrary commands are not accepted directly through requests
* request parameters are validated
* configuration is validated before use
* process execution has timeouts
* invalid reloads do not replace the active registry

Recommended future improvements:

* authentication for admin endpoints
* command allowlisting
* working directory restrictions
* environment variable restrictions
* audit logging
* role-based tool permissions
* stricter parameter typing
* safer placeholder handling
* sandboxed execution

## Known Limitations

* parameters are currently treated as strings
* placeholder replacement is simple
* example tools are Windows-oriented
* `tools.json` is resolved relative to the process working directory
* MCP support is intentionally minimal
* tool refresh behavior depends on the MCP client
* command output is returned after the process completes, not streamed

## Future Improvements

Possible next steps:

* support working directory per tool
* support environment variables per tool
* support cross-platform tool definitions
* support richer MCP resources
* support MCP prompts
* support stricter JSON Schema generation
* support streaming command output
* add authentication
* add Docker support
* provide a Rust implementation
