package io.github.milyor.qrcodeservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class QRCodeController {
    private final QRCodeGeneration qrCodeGeneration;
    private final ImageHandler imageHandler;

    public QRCodeController(@Autowired QRCodeGeneration qrCodeGeneration, @Autowired ImageHandler imageHandler) {
        this.qrCodeGeneration = qrCodeGeneration;
        this.imageHandler = imageHandler;
    }

    public ResponseEntity<?> getQRCode(
            @RequestParam(value = "contents", required = false) String contents,
            @RequestParam(value = "correction", required = false, defaultValue = "L") String correction,
            @RequestParam(value = "size", required = false, defaultValue = "250") Integer size,
            @RequestParam(value = "type", required = false, defaultValue = "png") String type) {

        if (contents == null || contents.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Contents cannot be null or blank"));
        }  else if (size == null || size < 150 || size> 350) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Image size must be between 150 and 350 pixels"));
        }else if (correction == null || !correction.equals("L") && !correction.equals("M") && !correction.equals("Q") && !correction.equals("H")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Permitted error correction levels are L, M, Q, H"));
        } else if (type == null || !type.equals("png") && !type.equals("jpeg") && !type.equals("jpg") && !type.equals("gif")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Only png, jpeg and gif image types are supported"));
        }

        BufferedImage bufferedImage = qrCodeGeneration.createQRCode(size, contents, correction);
        try {

            byte[] imageBytes = imageHandler.writeImageToByteArray(bufferedImage, type);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/" + type))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Only png, jpeg and gif image types are supported"));
        }
    }

}


