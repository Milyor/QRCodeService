package io.github.milyor.qrcodeservice.dto;

import jakarta.validation.constraints.*;

public class QRCodeRequest {
    @NotBlank(message = "Contents cannot be null or blank")
    private String content;

    @Pattern(regexp = "^[LMQH]$", message = "Permitted error correction levels are L, M, Q, H")
    private String correctionLevel;

    @Min(value = 150, message = "Image size must be at least 150 pixels")
    @Max(value = 350, message = "Image size must not exceed 350 pixels")
    private Integer size;

    @Pattern(regexp = "^(png|jpg|jpeg|gif)$", message = "Only png, jpeg, jpg and gif")
    private String type;

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCorrectionLevel() { return correctionLevel; }
    public void setCorrectionLevel(String correctionLevel) { this.correctionLevel = correctionLevel; }

}
