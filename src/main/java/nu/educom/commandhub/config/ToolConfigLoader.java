package nu.educom.commandhub.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nu.educom.commandhub.model.ToolDefinition;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
public class ToolConfigLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path toolsConfigPath = Path.of("tools.json");

    public List<ToolDefinition> loadTools() {
        try {
            return objectMapper.readValue(
                    toolsConfigPath.toFile(),
                    new TypeReference<List<ToolDefinition>>() {}
            );
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load tools configuration", exception);
        }
    }
}