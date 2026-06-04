package nu.educom.commandhub.model;

public record ReloadToolsResponse(
        String status,
        int toolCount
) {
}