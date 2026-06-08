package nu.educom.commandhub.service;

import nu.educom.commandhub.execution.ProcessExecutionResult;
import nu.educom.commandhub.execution.ProcessExecutor;
import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ExecuteToolResponse;
import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.registry.ToolRegistry;
import nu.educom.commandhub.validation.ToolRequestValidator;
import nu.educom.commandhub.exception.ToolNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToolExecutionService {

    private final ToolRegistry toolRegistry;
    private final ProcessExecutor processExecutor;
    private final ToolRequestValidator toolRequestValidator;
    private static final Logger logger = LoggerFactory.getLogger(ToolExecutionService.class);

    public ToolExecutionService(
            ToolRegistry toolRegistry,
            ProcessExecutor processExecutor,
            ToolRequestValidator toolRequestValidator
    ) {
        this.toolRegistry = toolRegistry;
        this.processExecutor = processExecutor;
        this.toolRequestValidator = toolRequestValidator;
    }

    public ExecuteToolResponse execute(String toolName, ExecuteToolRequest request) {
        logger.info("Executing tool: {}", toolName);
        ToolDefinition tool = toolRegistry.findByName(toolName)
                .orElseThrow(() -> new ToolNotFoundException(toolName));

        toolRequestValidator.validate(tool, request);

        List<String> command = buildCommand(tool, request);

        ProcessExecutionResult result = processExecutor.execute(command, tool.timeout());

        String status = result.exitCode() == 0 && !result.timedOut()
                ? "success"
                : "failed";

        logger.info(
                "Tool '{}' finished with status={}, exitCode={}, durationMs={}, timedOut={}",
                tool.name(),
                status,
                result.exitCode(),
                result.durationMs(),
                result.timedOut()
        );

        return new ExecuteToolResponse(
                tool.name(),
                status,
                result.stdout(),
                result.stderr(),
                result.exitCode(),
                result.durationMs(),
                result.timedOut()
        );
    }

    private List<String> buildCommand(ToolDefinition tool, ExecuteToolRequest request) {
        List<String> command = new ArrayList<>();

        command.add(tool.command());

        for (String argument : tool.arguments()) {
            command.add(replacePlaceholders(argument, request));
        }

        return command;
    }

    private String replacePlaceholders(String argument, ExecuteToolRequest request) {
        String result = argument;

        for (var entry : request.parameters().entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, entry.getValue());
        }

        return result;
    }
}