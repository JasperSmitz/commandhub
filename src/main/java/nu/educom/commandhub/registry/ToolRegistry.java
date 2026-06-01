package nu.educom.commandhub.registry;

import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.model.ToolParameter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ToolRegistry {

    private final List<ToolDefinition> tools = List.of(
            new ToolDefinition(
                    "echo",
                    "Echo a message",
                    "cmd.exe",
                    List.of("/c", "echo", "{message}"),
                    List.of(
                            new ToolParameter("message", "string", true)
                    ),
                    5000
            )
    );

    public List<ToolDefinition> findAll() {
        return tools;
    }

    public Optional<ToolDefinition> findByName(String name) {
        return tools.stream()
                .filter(tool -> tool.name().equalsIgnoreCase(name))
                .findFirst();
    }
}