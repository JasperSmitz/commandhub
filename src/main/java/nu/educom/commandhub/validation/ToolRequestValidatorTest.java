package nu.educom.commandhub.validation;

import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToolRequestValidatorTest {

    private final ToolRequestValidator validator = new ToolRequestValidator();

    @Test
    void validRequiredParameterPasses() {
        ToolDefinition tool = echoTool();
        ExecuteToolRequest request = new ExecuteToolRequest(
                Map.of("message", "hello")
        );

        assertDoesNotThrow(() -> validator.validate(tool, request));
    }

    @Test
    void missingParametersObjectThrowsException() {
        ToolDefinition tool = echoTool();
        ExecuteToolRequest request = new ExecuteToolRequest(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(tool, request)
        );

        assertEquals("Request field 'parameters' is required", exception.getMessage());
    }

    @Test
    void missingRequiredParameterThrowsException() {
        ToolDefinition tool = echoTool();
        ExecuteToolRequest request = new ExecuteToolRequest(Map.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(tool, request)
        );

        assertEquals("Missing required parameter: message", exception.getMessage());
    }

    @Test
    void blankRequiredParameterThrowsException() {
        ToolDefinition tool = echoTool();
        ExecuteToolRequest request = new ExecuteToolRequest(
                Map.of("message", "   ")
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(tool, request)
        );

        assertEquals("Missing required parameter: message", exception.getMessage());
    }

    @Test
    void optionalMissingParameterPasses() {
        ToolDefinition tool = new ToolDefinition(
                "optional-test",
                "Tool with optional parameter",
                "cmd.exe",
                List.of(),
                List.of(new ToolParameter("message", "string", false)),
                5000
        );

        ExecuteToolRequest request = new ExecuteToolRequest(Map.of());

        assertDoesNotThrow(() -> validator.validate(tool, request));
    }

    private ToolDefinition echoTool() {
        return new ToolDefinition(
                "echo",
                "Echo a message",
                "cmd.exe",
                List.of("/c", "echo", "{message}"),
                List.of(new ToolParameter("message", "string", true)),
                5000
        );
    }
}