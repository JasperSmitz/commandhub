package nu.educom.commandhub.service;

import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ExecuteToolResponse;
import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.registry.ToolRegistry;
import org.springframework.stereotype.Service;

@Service
public class ToolExecutionService {

    private final ToolRegistry toolRegistry;

    public ToolExecutionService(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    public ExecuteToolResponse execute(String toolName, ExecuteToolRequest request) {
        ToolDefinition tool = toolRegistry.findByName(toolName)
                .orElseThrow(() -> new IllegalArgumentException("Tool not found: " + toolName));

        String message = request.parameters().getOrDefault("message", "");

        return new ExecuteToolResponse(
                tool.name(),
                "success",
                message
        );
    }
}