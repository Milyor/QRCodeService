package io.github.milyor.qrcodeservice.controller;

import io.github.milyor.qrcodeservice.dto.ErrorResponse;
import io.github.milyor.qrcodeservice.util.ImageHandler;
import io.github.milyor.qrcodeservice.service.QRCodeGeneration;
import io.github.milyor.qrcodeservice.dto.QRCodeRequest;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);
    private final QRCodeGeneration qrCodeGeneration;
    private final ImageHandler imageHandler;

    @Value("${qrcode.default.size}")
    private int defaultSize;

    @Value("${qrcode.default.correction}")
    private String defaultCorrectionLevel;

    @Value("${qrcode.allowed.types}")
    private String allowedTypesValue;

    private Set<String> allowedTypesSet;

    public QRCodeController(@Autowired QRCodeGeneration qrCodeGeneration, @Autowired ImageHandler imageHandler) {
        this.qrCodeGeneration = qrCodeGeneration;
        this.imageHandler = imageHandler;
    }

    @PostConstruct
    public void init() {
        this.allowedTypesSet = Set.of(allowedTypesValue.toLowerCase().split(","));
        logger.info("Initialized allowed QR code types: {}", allowedTypesSet);
    }

    @GetMapping
    public ResponseEntity<?> getQRCode(@Valid QRCodeRequest request) {
        logger.info("Received QR code request for type: {}", request.getType());
        int size = (request.getSize() != null) ? request.getSize() : defaultSize;
        String correctionLevel = (request.getCorrectionLevel() != null && !request.getCorrectionLevel().isBlank())
                ? request.getCorrectionLevel().toUpperCase()
                : defaultCorrectionLevel;
        String requestedType = (request.getType() != null) ? request.getType().toLowerCase() : "";

        logger.info("Processing QR code request: Size={}, Level={}, Type={}", size, correctionLevel,requestedType);

        try {
            BufferedImage bufferedImage = qrCodeGeneration.createQRCode(
                    request.getSize(),
                    request.getContent(),
                    request.getCorrectionLevel()
            );

            if (bufferedImage == null) {
                logger.error("QR code generation returned null for content starting with: {}",
                        request.getContent().substring(0, Math.min(request.getContent().length(), 50)));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("Failed to generate QR code image"));
            }
            logger.debug("Successfully generated BufferedImage");

            byte[] imageBytes = imageHandler.writeImageToByteArray(bufferedImage, request.getType());

            logger.info("successfully processed QR code request for type: {}", request.getType());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/" + request.getType()))
                    .body(imageBytes);

        } catch (IOException e) {
            logger.error("IOException occurred while writing image bytes for type {}", request.getType(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Only png, jpeg and gif image types are supported"));
        }
    }

}


