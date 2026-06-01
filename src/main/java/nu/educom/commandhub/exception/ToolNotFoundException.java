package nu.educom.commandhub.exception;

public class ToolNotFoundException extends RuntimeException {

    public ToolNotFoundException(String toolName) {
        super("Tool not found: " + toolName);
    }
}