package io.github.milyor.qrcodeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Parameters for requesting a QR code")
public class QRCodeRequest {

    @NotBlank(message = "Contents cannot be null or blank")
    @Schema(description = "Text or fata to encode in the QR code",
    example = "https://github.com/Milyor",
    requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Pattern(regexp = "^[LMQH]$", message = "Permitted error correction levels are L, M, Q, H")
    @Schema(
            description = "Error correction level (L, M, Q, H). Default determined by server if omitted.",
            example = "L",
            pattern = "^[LMQH$]")
    private String correctionLevel;

    @Min(value = 150, message = "Image size must be at least 150 pixels")
    @Max(value = 350, message = "Image size must not exceed 350 pixels")
    @Schema(description = "Width and height of the QR code image in pixels. Default determined by server if omitted.",
            example = "250",
            minimum = "150",
            maximum = "350")
    private Integer size;

    @Pattern(regexp = "^(png|jpg|jpeg|gif)$", message = "Only png, jpeg, jpg and gif")
    @Schema(description = "Desired image output format (png, jpg, jpeg, gif). Default determined by server if omitted.",
            example = "png",
            pattern = "^(png|jpg|jpeg|gif)$")
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
