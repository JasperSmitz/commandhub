package nu.educom.commandhub.execution;

public record ProcessExecutionResult(
        String stdout,
        String stderr,
        int exitCode,
        long durationMs,
        boolean timedOut
) {
}