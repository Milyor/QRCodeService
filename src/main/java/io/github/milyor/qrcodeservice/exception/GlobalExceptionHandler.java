package io.github.milyor.qrcodeservice.exception;

import io.github.milyor.qrcodeservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.io.IOException;
import java.util.stream.Collectors;

@ControllerAdvice // To handle global exceptions
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger hdLogger = LoggerFactory.getLogger(GlobalExceptionHandler.class);




    // Handle our custom QR code generation errors
    @ExceptionHandler(QRCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQrCodeGenerationFailure(QRCodeGenerationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("Failed to generate QR code image due to an internal error.");
        // Log the root cause for debugging
        hdLogger.error("QR Code generation failed: Request: {}", request.getDescription(false), ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle IOExceptions (e.g., from ImageHandler)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("An internal error occurred while processing the image.");
        hdLogger.error("IOException occurred processing request: {}", request.getDescription(false), ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Fallback for any other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("An unexpected error occurred.");
        hdLogger.error("Unexpected error processing request: {}", request.getDescription(false), ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(error, headers, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    // Specific Spring Validation Exceptions like @Valid, @Min, @Max
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        hdLogger.warn("Validation failure: {} for request: {}", ex.getMessage(), request.getDescription(false)); // Log the internal details

        // Extract user-friendly error messages from the exception
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        //  Standard error response DTO
        ErrorResponse errorResponse = new ErrorResponse("Validation failed: " + errors);

        // Return a full HTTP response: JSON body, specific headers, 400 status
        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
}
