package nu.educom.commandhub.registry;

import nu.educom.commandhub.config.ToolConfigLoader;
import nu.educom.commandhub.model.ToolDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ToolRegistry {

    private final ToolConfigLoader toolConfigLoader;
    private volatile List<ToolDefinition> tools;

    public ToolRegistry(ToolConfigLoader toolConfigLoader) {
        this.toolConfigLoader = toolConfigLoader;
        this.tools = List.copyOf(toolConfigLoader.loadTools());
    }

    public List<ToolDefinition> findAll() {
        return tools;
    }

    public Optional<ToolDefinition> findByName(String name) {
        return tools.stream()
                .filter(tool -> tool.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public int reload() {
        List<ToolDefinition> newTools = List.copyOf(toolConfigLoader.loadTools());
        this.tools = newTools;
        return newTools.size();
    }
}