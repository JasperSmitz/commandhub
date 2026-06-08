package nu.educom.commandhub.mcp;

import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class McpToolAdapter {

    public Map<String, Object> toMcpTool(ToolDefinition tool) {
        Map<String, Object> mcpTool = new LinkedHashMap<>();

        mcpTool.put("name", tool.name());
        mcpTool.put("description", tool.description());
        mcpTool.put("inputSchema", toInputSchema(tool.parameters()));

        return mcpTool;
    }

    private Map<String, Object> toInputSchema(List<ToolParameter> parameters) {
        Map<String, Object> schema = new LinkedHashMap<>();
        Map<String, Object> properties = new LinkedHashMap<>();

        List<String> required = parameters.stream()
                .filter(ToolParameter::required)
                .map(ToolParameter::name)
                .toList();

        for (ToolParameter parameter : parameters) {
            Map<String, Object> property = new LinkedHashMap<>();
            property.put("type", mapParameterType(parameter.type()));
            properties.put(parameter.name(), property);
        }

        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required);

        return schema;
    }

    private String mapParameterType(String type) {
        return switch (type.toLowerCase()) {
            case "number", "integer", "boolean", "string" -> type.toLowerCase();
            default -> "string";
        };
    }
}