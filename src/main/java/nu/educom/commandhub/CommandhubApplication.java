package nu.educom.commandhub;

import org.springframework.boot.SpringApplication;
import nu.educom.commandhub.mcp.McpStdioServer;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommandhubApplication {

	public static void main(String[] args) {
		boolean mcpStdioMode = args.length > 0 && "--mcp-stdio".equals(args[0]);

		if (mcpStdioMode) {
			var context = new SpringApplicationBuilder(CommandhubApplication.class)
					.web(WebApplicationType.NONE)
					.profiles("mcp")
					.logStartupInfo(false)
					.run(args);

			context.getBean(McpStdioServer.class).run();

			context.close();
			return;
		}

		new SpringApplicationBuilder(CommandhubApplication.class)
				.run(args);
	}
}
