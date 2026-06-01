package nu.educom.commandhub.model;

public record ToolParameter(
        String name,
        String type,
        boolean required
) {
}