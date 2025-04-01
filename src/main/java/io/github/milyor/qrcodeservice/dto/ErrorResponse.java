package io.github.milyor.qrcodeservice.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard JSON response format for API errors")
public class ErrorResponse {

    @Schema(description = "Detailed error message explaining what went wrong",
            example = "Validation failed: content: Contents cannot be null or blank")
    public String error;

    public ErrorResponse(String error) {this.error = error;}
    public String getError() {return error;}
    public void setError(String error) {this.error = error;}
}
