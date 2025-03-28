package io.github.milyor.qrcodeservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageHandler {
    private static final Logger log = LoggerFactory.getLogger(ImageHandler.class);
    public byte[] writeImageToByteArray(BufferedImage bufferedImage, String type) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        log.debug("Writing image to byte  array as type {}", type);
        ImageIO.write(bufferedImage, type, out);
        byte[] bytes = out.toByteArray();
        log.debug("Image size in bytes: {}", bytes.length);
        return out.toByteArray();
    }
}
