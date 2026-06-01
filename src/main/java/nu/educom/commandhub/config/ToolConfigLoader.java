package nu.educom.commandhub.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nu.educom.commandhub.model.ToolDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ToolConfigLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ToolDefinition> loadTools() {
        try {
            ClassPathResource resource = new ClassPathResource("tools.json");

            try (InputStream inputStream = resource.getInputStream()) {
                return objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<ToolDefinition>>() {}
                );
            }

        } catch (IOException exception) {
            throw new RuntimeException("Failed to load tools configuration", exception);
        }
    }
}