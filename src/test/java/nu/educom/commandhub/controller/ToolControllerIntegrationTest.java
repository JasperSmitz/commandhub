package nu.educom.commandhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ToolControllerIntegrationTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Test
    void getToolsReturns200() {
        assertThat(mockMvc.get().uri("/tools"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].name")
                .isEqualTo("echo");
    }

    @Test
    void validToolExecutionReturns200() {
        String requestBody = """
                {
                  "parameters": {
                    "message": "hello"
                  }
                }
                """;

        assertThat(mockMvc.post().uri("/tools/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.status")
                .isEqualTo("success");
    }

    @Test
    void unknownToolReturns404() {
        String requestBody = """
                {
                  "parameters": {
                    "message": "hello"
                  }
                }
                """;

        assertThat(mockMvc.post().uri("/tools/notreal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatus(404)
                .bodyJson()
                .extractingPath("$.error")
                .isEqualTo("not_found");
    }

    @Test
    void missingRequiredParameterReturns400() {
        String requestBody = """
                {
                  "parameters": {}
                }
                """;

        assertThat(mockMvc.post().uri("/tools/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatus(400)
                .bodyJson()
                .extractingPath("$.error")
                .isEqualTo("bad_request");
    }
}