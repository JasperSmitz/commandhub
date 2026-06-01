package nu.educom.commandhub.controller;

import nu.educom.commandhub.model.ExecuteToolRequest;
import nu.educom.commandhub.model.ExecuteToolResponse;
import nu.educom.commandhub.model.ToolDefinition;
import nu.educom.commandhub.registry.ToolRegistry;
import nu.educom.commandhub.service.ToolExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tools")
public class ToolController {

    private final ToolRegistry toolRegistry;
    private final ToolExecutionService toolExecutionService;

    public ToolController(
            ToolRegistry toolRegistry,
            ToolExecutionService toolExecutionService
    ) {
        this.toolRegistry = toolRegistry;
        this.toolExecutionService = toolExecutionService;
    }

    @GetMapping
    public List<ToolDefinition> getTools() {
        return toolRegistry.findAll();
    }

    @PostMapping("/{toolName}")
    public ExecuteToolResponse executeTool(
            @PathVariable String toolName,
            @RequestBody ExecuteToolRequest request
    ) {
        return toolExecutionService.execute(toolName, request);
    }
}