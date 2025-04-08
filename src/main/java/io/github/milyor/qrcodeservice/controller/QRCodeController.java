package io.github.milyor.qrcodeservice.controller;

import io.github.milyor.qrcodeservice.dto.ErrorResponse;
import io.github.milyor.qrcodeservice.entity.QRCodeRecord;
import io.github.milyor.qrcodeservice.repo.QRCodeRecordRepo;
import io.github.milyor.qrcodeservice.util.ImageHandler;
import io.github.milyor.qrcodeservice.service.QRCodeGeneration;
import io.github.milyor.qrcodeservice.dto.QRCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/qrcode")
@Tag(name = "QR Code")
public class QRCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);
    private final QRCodeGeneration qrCodeGeneration;
    private final ImageHandler imageHandler;
    private final QRCodeRecordRepo qrCodeRecordRepo;

    @Value("${qrcode.default.size}")
    private int defaultSize;

    @Value("${qrcode.default.correction}")
    private String defaultCorrectionLevel;

    @Value("${qrcode.allowed.types}")
    private String allowedTypesValue;

    @Value("${qrcode.default.type}")
    private String defaultTypeValue;

    private Set<String> allowedTypesSet;

    public QRCodeController(@Autowired QRCodeGeneration qrCodeGeneration, @Autowired ImageHandler imageHandler, @Autowired QRCodeRecordRepo qrCodeRecordRepo) {
        this.qrCodeGeneration = qrCodeGeneration;
        this.imageHandler = imageHandler;
        this.qrCodeRecordRepo = qrCodeRecordRepo;
    }

    @PostConstruct
    public void init() {
        this.allowedTypesSet = Set.of(allowedTypesValue.toLowerCase().split(","));
        logger.info("Initialized allowed QR code types: {}", allowedTypesSet);
    }
    @Operation(
            description = "Get endpoint to generate QR Code",
            responses = {
                    @ApiResponse(
                            description = "Success - Returns the generated QR code image.",
                            responseCode = "200",
                            content = {
                                    @Content(mediaType = "image/png"),
                                    @Content(mediaType = "image/jpeg"),
                                    @Content(mediaType = "image/gif"),

                            }
                    ),
                    @ApiResponse(
                            description = "Bad Request - Invalid parameters supplied (e.g., missing content, invalid type/size/level).",
                            responseCode = "400",
                            content = {
                                    @Content(mediaType = "application/Json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                            }
                    ),
                    @ApiResponse(
                            description = "Internal Server Error - Failed during QR code generation or image processing.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping
    @Transactional
    public ResponseEntity<byte[]> getQRCode(@Valid QRCodeRequest request) throws IOException {

        logger.info("Received QR code request for type: {}", request.getType());

        int size = (request.getSize() != null) ? request.getSize() : defaultSize;
        String correctionLevel = (request.getCorrectionLevel() != null && !request.getCorrectionLevel().isBlank())
                ? request.getCorrectionLevel().toUpperCase()
                : defaultCorrectionLevel;
        String requestedType = (request.getType() != null) ? request.getType().toLowerCase() : defaultTypeValue;

        logger.info("Processing QR code request: Size={}, Level={}, Type={}", size, correctionLevel,defaultTypeValue);

        if (requestedType.isBlank() || !allowedTypesSet.contains(requestedType)) {
            logger.warn("Unsupported image type {}", requestedType);
            throw new IllegalArgumentException("Unsupported image type " + requestedType + ". Allowed types are: " + allowedTypesValue);
        }

        String imageIOType = requestedType.equals("jpg") ? "jpeg" : requestedType;
        // Save metaData of the qrCode
        try {
            QRCodeRecord qrcode = new QRCodeRecord(request.getContent(), size, correctionLevel, imageIOType, LocalDateTime.now());
            QRCodeRecord savedRecord = qrCodeRecordRepo.save(qrcode);
            logger.info("Saved QR code record: {}", savedRecord);
        } catch (Exception e) {
            logger.error("Error savin QR Code metaData for content starting with type: {}", request.getContent().substring(0, Math.min(request.getContent().length(), 50)), e);
        }

        BufferedImage bufferedImage = qrCodeGeneration.createQRCode(
                size,
                request.getContent(),
                correctionLevel
        );
        logger.debug("Successfully generated BufferedImage");

        byte[] imageBytes = imageHandler.writeImageToByteArray(bufferedImage, imageIOType);

        logger.info("successfully processed QR code request for type: {}", requestedType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/" + imageIOType))
                .body(imageBytes);

        }

    }


