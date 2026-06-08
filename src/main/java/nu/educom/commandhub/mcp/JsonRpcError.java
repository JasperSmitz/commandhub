package nu.educom.commandhub.mcp;

public record JsonRpcError(
        int code,
        String message
) {
}