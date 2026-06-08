package nu.educom.commandhub.registry;

import nu.educom.commandhub.config.ToolConfigLoader;
import nu.educom.commandhub.config.ToolConfigValidator;
import nu.educom.commandhub.model.ToolDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ToolRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ToolRegistry.class);

    private final ToolConfigLoader toolConfigLoader;
    private final ToolConfigValidator toolConfigValidator;

    private volatile List<ToolDefinition> tools;

    public ToolRegistry(
            ToolConfigLoader toolConfigLoader,
            ToolConfigValidator toolConfigValidator
    ) {
        this.toolConfigLoader = toolConfigLoader;
        this.toolConfigValidator = toolConfigValidator;

        List<ToolDefinition> loadedTools = List.copyOf(toolConfigLoader.loadTools());
        toolConfigValidator.validate(loadedTools);

        this.tools = loadedTools;

        logger.info("Loaded {} tools from configuration", tools.size());
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
        logger.info("Reloading tools configuration");

        List<ToolDefinition> newTools = List.copyOf(toolConfigLoader.loadTools());

        toolConfigValidator.validate(newTools);

        this.tools = newTools;

        logger.info("Reloaded {} tools", newTools.size());

        return newTools.size();
    }
}