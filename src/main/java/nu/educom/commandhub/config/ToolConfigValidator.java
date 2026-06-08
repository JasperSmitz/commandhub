package nu.educom.commandhub.config;

import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ToolConfigValidator {

    public void validate(List<ToolDefinition> tools) {
        if (tools == null) {
            throw new IllegalArgumentException("Tools configuration must not be null");
        }

        Set<String> toolNames = new HashSet<>();

        for (ToolDefinition tool : tools) {
            validateTool(tool, toolNames);
        }
    }

    private void validateTool(ToolDefinition tool, Set<String> toolNames) {
        if (tool == null) {
            throw new IllegalArgumentException("Tool definition must not be null");
        }

        if (tool.name() == null || tool.name().isBlank()) {
            throw new IllegalArgumentException("Tool name is required");
        }

        String normalizedToolName = tool.name().toLowerCase();

        if (!toolNames.add(normalizedToolName)) {
            throw new IllegalArgumentException("Duplicate tool name: " + tool.name());
        }

        if (tool.description() == null || tool.description().isBlank()) {
            throw new IllegalArgumentException("Tool description is required for tool: " + tool.name());
        }

        if (tool.command() == null || tool.command().isBlank()) {
            throw new IllegalArgumentException("Tool command is required for tool: " + tool.name());
        }

        if (tool.arguments() == null) {
            throw new IllegalArgumentException("Tool arguments must not be null for tool: " + tool.name());
        }

        if (tool.parameters() == null) {
            throw new IllegalArgumentException("Tool parameters must not be null for tool: " + tool.name());
        }

        if (tool.timeout() <= 0) {
            throw new IllegalArgumentException("Tool timeout must be greater than 0 for tool: " + tool.name());
        }

        validateParameters(tool);
    }

    private void validateParameters(ToolDefinition tool) {
        Set<String> parameterNames = new HashSet<>();

        for (ToolParameter parameter : tool.parameters()) {
            if (parameter == null) {
                throw new IllegalArgumentException("Parameter definition must not be null for tool: " + tool.name());
            }

            if (parameter.name() == null || parameter.name().isBlank()) {
                throw new IllegalArgumentException("Parameter name is required for tool: " + tool.name());
            }

            String normalizedParameterName = parameter.name().toLowerCase();

            if (!parameterNames.add(normalizedParameterName)) {
                throw new IllegalArgumentException(
                        "Duplicate parameter name '" + parameter.name() + "' for tool: " + tool.name()
                );
            }

            if (parameter.type() == null || parameter.type().isBlank()) {
                throw new IllegalArgumentException(
                        "Parameter type is required for parameter '" + parameter.name() + "' in tool: " + tool.name()
                );
            }
        }
    }
}