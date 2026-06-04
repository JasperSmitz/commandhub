package nu.educom.commandhub.controller;

import nu.educom.commandhub.model.ReloadToolsResponse;
import nu.educom.commandhub.registry.ToolRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final ToolRegistry toolRegistry;

    public AdminController(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @PostMapping("/admin/reload-tools")
    public ReloadToolsResponse reloadTools() {
        int toolCount = toolRegistry.reload();

        return new ReloadToolsResponse(
                "success",
                toolCount
        );
    }
}