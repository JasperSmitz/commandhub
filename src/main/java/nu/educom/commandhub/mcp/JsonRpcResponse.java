package nu.educom.commandhub.mcp;

public record JsonRpcResponse(
        String jsonrpc,
        Object id,
        Object result,
        JsonRpcError error
) {
    public static JsonRpcResponse success(Object id, Object result) {
        return new JsonRpcResponse("2.0", id, result, null);
    }

    public static JsonRpcResponse error(Object id, int code, String message) {
        return new JsonRpcResponse("2.0", id, null, new JsonRpcError(code, message));
    }
}