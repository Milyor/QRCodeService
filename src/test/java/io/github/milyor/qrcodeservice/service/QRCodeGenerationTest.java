package io.github.milyor.qrcodeservice.service;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.awt.image.BufferedImage;

public class QRCodeGenerationTest {
    private final QRCodeGeneration qrCodeGeneration = new QRCodeGeneration();

    @Test
    void createQRCodeValidInput() {
        int size = 200;
        String contents = "Hello World";
        String level = "L";

        BufferedImage resultImage = catchException(() ->
                qrCodeGeneration.createQRCode(size, contents, level)
        ) == null ? qrCodeGeneration.createQRCode(size, contents, level) : null; // Re-run to get image if no exception
        assertThat(resultImage).isNotNull();
        assertThat(resultImage.getWidth()).isEqualTo(size);
        assertThat(resultImage.getHeight()).isEqualTo(size);
    }

    @Test
    void createQRCodeInvalidLevel() {
        String invalidLevel = "X";

        assertThatThrownBy(()-> qrCodeGeneration.createQRCode(200, "test", invalidLevel)).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(invalidLevel);
    }

    @Test
    void createQRCodeWithNullLevel() {
        String nullLevel = null;

        assertThatThrownBy(()-> qrCodeGeneration.createQRCode(200, "test", nullLevel)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void createQRCodeWithNullContents() {
        String nullContents = null;
        assertThatThrownBy(()-> qrCodeGeneration.createQRCode(200, nullContents, "L"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void createQRCodeWithZeroSize() {
        int zeroSize = 0;
        int expectedSize = 29; // The size ZXing produces when input is 0
        BufferedImage resultImage;

        assertThatCode(()-> qrCodeGeneration.createQRCode(zeroSize, "test", "L")).doesNotThrowAnyException();

        resultImage = qrCodeGeneration.createQRCode(zeroSize, "test", "L");

        assertThat(resultImage).isNotNull();
        assertThat(resultImage.getWidth()).isEqualTo(expectedSize);
        assertThat(resultImage.getHeight()).isEqualTo(expectedSize);
    }

    @Test
    void createQRCodeWithNegativeSize() {
        int negativeSize = -1;
        assertThatThrownBy(()-> qrCodeGeneration.createQRCode(negativeSize, "test", "L")).isInstanceOf(IllegalArgumentException.class);
    }

}
