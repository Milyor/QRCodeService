package io.github.milyor.qrcodeservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class ImageHandlerTest {
    private ImageHandler imageHandler;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        imageHandler = new ImageHandler();
        testImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    }

    @Test
    void writeImageToByteArray_ValidTypePNG() {
        String type = "png";
        byte[] resultBytes = null;
        try {
            resultBytes = imageHandler.writeImageToByteArray(testImage, type);
        } catch (IOException e) {
            fail("Should not throw IOException for valid type 'png'", e);
        }

        assertThat(resultBytes).isNotNull();
        assertThat(resultBytes).isNotEmpty();

    }

    @Test
    void writeImageToByteArray_withValidJpgType_shouldReturnNonEmptyByteArray() {

        String validFormat = "jpg";

        byte[] resultBytes;
        assertThatCode(() -> imageHandler.writeImageToByteArray(testImage, validFormat)).doesNotThrowAnyException();

        try {
            resultBytes = imageHandler.writeImageToByteArray(testImage, validFormat);
            assertThat(resultBytes).isNotNull().isNotEmpty();
        } catch (IOException e) {
            fail("Should not have thrown IOException for valid type 'jpg'", e);
        }
    }

    @Test
    void writeImageToByteArray_withNullImage_shouldThrowException() {

        BufferedImage nullImage = null;
        String format = "png";

        assertThatThrownBy(() -> imageHandler.writeImageToByteArray(nullImage, format)).isInstanceOf(IllegalArgumentException.class);
    }


}
