package nu.educom.commandhub.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ExecuteToolResponse;
import nu.educom.commandhub.registry.ToolRegistry;
import nu.educom.commandhub.service.ToolExecutionService;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class McpStdioServer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ToolRegistry toolRegistry;
    private final ToolExecutionService toolExecutionService;
    private final McpToolAdapter mcpToolAdapter;

    public McpStdioServer(
            ToolRegistry toolRegistry,
            ToolExecutionService toolExecutionService,
            McpToolAdapter mcpToolAdapter
    ) {
        this.toolRegistry = toolRegistry;
        this.toolExecutionService = toolExecutionService;
        this.mcpToolAdapter = mcpToolAdapter;
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                JsonRpcRequest request = objectMapper.readValue(line, JsonRpcRequest.class);

                if (isNotification(request)) {
                    handleNotification(request);
                    continue;
                }

                JsonRpcResponse response = handleRequest(request);

                System.out.println(objectMapper.writeValueAsString(response));
                System.out.flush();
            }
        } catch (Exception exception) {
            // In stdio mode stdout is protocol-only. Use stderr for fatal diagnostics.
            System.err.println("MCP stdio server failed: " + exception.getMessage());
        }
    }

    private boolean isNotification(JsonRpcRequest request) {
        return request.id() == null;
    }

    private void handleNotification(JsonRpcRequest request) {
        // notifications/initialized does not require a response.
    }

    private JsonRpcResponse handleRequest(JsonRpcRequest request) {
        try {
            return switch (request.method()) {
                case "initialize" -> JsonRpcResponse.success(request.id(), initializeResult());
                case "tools/list" -> JsonRpcResponse.success(request.id(), toolsListResult());
                case "tools/call" -> JsonRpcResponse.success(request.id(), toolsCallResult(request.params()));
                case "resources/list" -> JsonRpcResponse.success(request.id(), resourcesListResult());
                case "resources/read" -> JsonRpcResponse.success(request.id(), resourcesReadResult(request.params()));
                default -> JsonRpcResponse.error(request.id(), -32601, "Method not found: " + request.method());
            };
        } catch (IllegalArgumentException exception) {
            return JsonRpcResponse.error(request.id(), -32602, exception.getMessage());
        } catch (Exception exception) {
            return JsonRpcResponse.error(request.id(), -32603, exception.getMessage());
        }
    }

    private Map<String, Object> initializeResult() {
        Map<String, Object> capabilities = new LinkedHashMap<>();
        capabilities.put("tools", Map.of());
        capabilities.put("resources", Map.of());

        Map<String, Object> serverInfo = new LinkedHashMap<>();
        serverInfo.put("name", "commandhub");
        serverInfo.put("version", "0.0.1");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocolVersion", "2025-06-18");
        result.put("capabilities", capabilities);
        result.put("serverInfo", serverInfo);

        return result;
    }

    private Map<String, Object> toolsListResult() {
        var tools = toolRegistry.findAll()
                .stream()
                .map(mcpToolAdapter::toMcpTool)
                .toList();

        return Map.of("tools", tools);
    }

    private Map<String, Object> toolsCallResult(JsonNode params) {
        if (params == null || params.get("name") == null) {
            throw new IllegalArgumentException("Tool call parameter 'name' is required");
        }

        String toolName = params.get("name").asText();

        Map<String, String> arguments = extractArguments(params.get("arguments"));

        ExecuteToolResponse executionResponse = toolExecutionService.execute(
                toolName,
                new ExecuteToolRequest(arguments)
        );

        String text = formatToolResult(executionResponse);

        return Map.of(
                "content",
                List.of(Map.of(
                        "type", "text",
                        "text", text
                )),
                "isError",
                !"success".equals(executionResponse.status())
        );
    }

    private Map<String, String> extractArguments(JsonNode argumentsNode) {
        if (argumentsNode == null || argumentsNode.isNull()) {
            return Map.of();
        }

        if (!argumentsNode.isObject()) {
            throw new IllegalArgumentException("Tool call parameter 'arguments' must be an object");
        }

        return objectMapper.convertValue(
                argumentsNode,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
        );
    }

    private String formatToolResult(ExecuteToolResponse response) {
        return """
                status: %s
                stdout:
                %s

                stderr:
                %s

                exitCode: %d
                durationMs: %d
                timedOut: %s
                """.formatted(
                response.status(),
                response.stdout(),
                response.stderr(),
                response.exitCode(),
                response.durationMs(),
                response.timedOut()
        );
    }
    private Map<String, Object> resourcesListResult() {
        return Map.of(
                "resources",
                List.of(
                        Map.of(
                                "uri", "commandhub://tools/config",
                                "name", "CommandHub tools configuration",
                                "description", "The current tools.json configuration used by CommandHub",
                                "mimeType", "application/json"
                        )
                )
        );
    }

    private Map<String, Object> resourcesReadResult(JsonNode params) {
        if (params == null || params.get("uri") == null) {
            throw new IllegalArgumentException("Resource read parameter 'uri' is required");
        }

        String uri = params.get("uri").asText();

        if (!"commandhub://tools/config".equals(uri)) {
            throw new IllegalArgumentException("Unknown resource URI: " + uri);
        }

        try {
            String text = Files.readString(Path.of("tools.json"));

            return Map.of(
                    "contents",
                    List.of(
                            Map.of(
                                    "uri", uri,
                                    "mimeType", "application/json",
                                    "text", text
                            )
                    )
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to read resource: " + exception.getMessage());
        }
    }
}