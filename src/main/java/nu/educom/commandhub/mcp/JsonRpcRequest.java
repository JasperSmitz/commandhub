package nu.educom.commandhub.mcp;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonRpcRequest(
        String jsonrpc,
        Object id,
        String method,
        JsonNode params
) {
}