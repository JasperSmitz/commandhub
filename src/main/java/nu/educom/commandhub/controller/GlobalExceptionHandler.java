package nu.educom.commandhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import nu.educom.commandhub.exception.ToolNotFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponse(
                "bad_request",
                exception.getMessage(),
                Instant.now().toString()
        );
    }

    @ExceptionHandler(ToolNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleToolNotFoundException(ToolNotFoundException exception) {
        return new ErrorResponse(
                "not_found",
                exception.getMessage(),
                Instant.now().toString()
        );
    }

    public record ErrorResponse(
            String error,
            String message,
            String timestamp
    ) {
    }
}