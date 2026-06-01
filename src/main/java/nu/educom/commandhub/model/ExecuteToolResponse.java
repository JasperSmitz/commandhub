package nu.educom.commandhub.model;

public record ExecuteToolResponse(
        String toolName,
        String status,
        String output
) {
}