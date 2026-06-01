package nu.educom.commandhub.validation;

import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.springframework.stereotype.Component;

@Component
public class ToolRequestValidator {

    public void validate(ToolDefinition tool, ExecuteToolRequest request) {

        for (ToolParameter parameter : tool.parameters()) {

            if (!parameter.required()) {
                continue;
            }

            String value = request.parameters().get(parameter.name());

            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing required parameter: " + parameter.name()
                );
            }
        }
    }
}