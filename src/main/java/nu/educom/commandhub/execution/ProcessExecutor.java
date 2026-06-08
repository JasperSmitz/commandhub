package nu.educom.commandhub.execution;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProcessExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);

    public ProcessExecutionResult execute(List<String> command, long timeoutMs) {
        logger.debug("Starting process: {}", command);
        long start = System.currentTimeMillis();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);

            if (!finished) {
                logger.warn("Process timed out after {}ms: {}", timeoutMs, command);

                process.destroyForcibly();

                long duration = System.currentTimeMillis() - start;

                return new ProcessExecutionResult(
                        "",
                        "Process timed out",
                        -1,
                        duration,
                        true
                );
            }

            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            long duration = System.currentTimeMillis() - start;

            return new ProcessExecutionResult(
                    stdout.strip(),
                    stderr.strip(),
                    process.exitValue(),
                    duration,
                    false
            );

        } catch (IOException exception) {
            logger.error("Failed to start process: {}", command, exception);
            long duration = System.currentTimeMillis() - start;

            return new ProcessExecutionResult(
                    "",
                    exception.getMessage(),
                    -1,
                    duration,
                    false
            );

        } catch (InterruptedException exception) {
            logger.warn("Process execution interrupted: {}", command, exception);
            Thread.currentThread().interrupt();

            long duration = System.currentTimeMillis() - start;

            return new ProcessExecutionResult(
                    "",
                    "Process execution was interrupted",
                    -1,
                    duration,
                    false
            );
        }
    }
}