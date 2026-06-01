package nu.educom.commandhub.model;

import java.util.Map;

public record ExecuteToolRequest(
        Map<String, String> parameters
) {
}