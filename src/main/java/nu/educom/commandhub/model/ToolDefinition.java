package nu.educom.commandhub.model;

import java.util.List;

public record ToolDefinition(
        String name,
        String description,
        String command,
        List<String> arguments,
        List<ToolParameter> parameters,
        long timeout
) {
}