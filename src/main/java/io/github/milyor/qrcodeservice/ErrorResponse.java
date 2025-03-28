package io.github.milyor.qrcodeservice;

public class ErrorResponse {
    public String error;

    public ErrorResponse(String error) {this.error = error;}
    public String getError() {return error;}
    public void setError(String error) {this.error = error;}
}
