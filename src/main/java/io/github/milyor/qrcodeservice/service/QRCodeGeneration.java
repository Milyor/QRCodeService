package io.github.milyor.qrcodeservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.github.milyor.qrcodeservice.exception.QRCodeGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Map;

@Service
public class QRCodeGeneration {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeGeneration.class);

    @Cacheable("QRCodes")
    public BufferedImage createQRCode(int size, String contents, String level) {
        logger.info(">>> Generating QR Code image for size={}, level={}, content='{}'",
                size, level, contents.substring(0, Math.min(contents.length(), 30)));
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, ?> hints = Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.valueOf(level.toUpperCase()));
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            String errorMsg = "Failed to encode QR code";
            logger.error(errorMsg, e);
            throw new QRCodeGenerationException(errorMsg, e);
        }
    }
}
