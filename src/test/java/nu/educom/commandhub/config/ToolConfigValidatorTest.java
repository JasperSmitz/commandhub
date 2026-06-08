package nu.educom.commandhub.config;

import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolConfigValidatorTest {

    private final ToolConfigValidator validator = new ToolConfigValidator();

    @Test
    void validToolConfigPasses() {
        List<ToolDefinition> tools = List.of(validEchoTool());

        assertDoesNotThrow(() -> validator.validate(tools));
    }

    @Test
    void emptyToolNameThrowsException() {
        ToolDefinition tool = new ToolDefinition(
                "",
                "Broken tool",
                "cmd.exe",
                List.of(),
                List.of(),
                5000
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(tool))
        );

        assertEquals("Tool name is required", exception.getMessage());
    }

    @Test
    void duplicateToolNamesThrowException() {
        ToolDefinition first = validEchoTool();
        ToolDefinition second = new ToolDefinition(
                "ECHO",
                "Duplicate echo tool",
                "cmd.exe",
                List.of("/c", "echo", "hello"),
                List.of(),
                5000
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(first, second))
        );

        assertEquals("Duplicate tool name: ECHO", exception.getMessage());
    }

    @Test
    void nullArgumentsThrowException() {
        ToolDefinition tool = new ToolDefinition(
                "broken",
                "Broken tool",
                "cmd.exe",
                null,
                List.of(),
                5000
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(tool))
        );

        assertEquals("Tool arguments must not be null for tool: broken", exception.getMessage());
    }

    @Test
    void emptyArgumentsAreAllowed() {
        ToolDefinition tool = new ToolDefinition(
                "java-version",
                "Show installed Java version",
                "cmd.exe",
                List.of(),
                List.of(),
                5000
        );

        assertDoesNotThrow(() -> validator.validate(List.of(tool)));
    }

    @Test
    void nullParametersThrowException() {
        ToolDefinition tool = new ToolDefinition(
                "broken",
                "Broken tool",
                "cmd.exe",
                List.of(),
                null,
                5000
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(tool))
        );

        assertEquals("Tool parameters must not be null for tool: broken", exception.getMessage());
    }

    @Test
    void emptyParametersAreAllowed() {
        ToolDefinition tool = new ToolDefinition(
                "java-version",
                "Show installed Java version",
                "cmd.exe",
                List.of("/c", "java", "-version"),
                List.of(),
                5000
        );

        assertDoesNotThrow(() -> validator.validate(List.of(tool)));
    }

    @Test
    void duplicateParameterNamesThrowException() {
        ToolDefinition tool = new ToolDefinition(
                "echo",
                "Echo a message",
                "cmd.exe",
                List.of("/c", "echo", "{message}"),
                List.of(
                        new ToolParameter("message", "string", true),
                        new ToolParameter("MESSAGE", "string", false)
                ),
                5000
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(tool))
        );

        assertEquals("Duplicate parameter name 'MESSAGE' for tool: echo", exception.getMessage());
    }

    @Test
    void timeoutMustBeGreaterThanZero() {
        ToolDefinition tool = new ToolDefinition(
                "broken",
                "Broken tool",
                "cmd.exe",
                List.of(),
                List.of(),
                0
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(List.of(tool))
        );

        assertEquals("Tool timeout must be greater than 0 for tool: broken", exception.getMessage());
    }

    private ToolDefinition validEchoTool() {
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