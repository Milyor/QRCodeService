package io.github.milyor.qrcodeservice;

import io.github.milyor.qrcodeservice.service.QRCodeGeneration;
import io.github.milyor.qrcodeservice.util.ImageHandler;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = QrCodeServiceApplication.class)
@AutoConfigureMockMvc
class QRCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Replace real bean with a Mockito mock
    private QRCodeGeneration mockQrCodeGeneration;

    @MockitoBean // Replace real bean with a Mockito mock
    private ImageHandler mockImageHandler;

    @Test
    void getQRCode_whenValidRequest_shouldCallServiceAndReturnOk() throws Exception {
        String testContent = "test-mock";
        String testType = "png";
        int expectedSize = 250;
        String expectedImageIOType = "png";

        // Arrange: Configure mocks for success path
        BufferedImage dummyImage = new BufferedImage(expectedSize, expectedSize, BufferedImage.TYPE_INT_RGB);
        byte[] dummyBytes = new byte[]{1, 2, 3}; // Dummy image bytes

        when(mockQrCodeGeneration.createQRCode(anyInt(), eq(testContent), anyString()))
                .thenReturn(dummyImage);
        when(mockImageHandler.writeImageToByteArray(eq(dummyImage), eq(expectedImageIOType)))
                .thenReturn(dummyBytes);


        // Act & Assert
        mockMvc.perform(get("/api/qrcode")
                        .param("content", testContent)
                        .param("type", testType)
                .with(httpBasic("user","password")))
                // Add other params like size/level if needed for the test case
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(dummyBytes));

        // Verify mocks were called (optional but good practice)
        verify(mockQrCodeGeneration).createQRCode(anyInt(), eq(testContent), anyString());
        verify(mockImageHandler).writeImageToByteArray(eq(dummyImage), eq(expectedImageIOType));
    }

    @Test
    void testWith_EmptyContent_shouldReturnBadRequest(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode").with(httpBasic("user","password"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Validation failed: content: Contents cannot be null or blank")));
    }

    @Test
    void testWithContent_NoOtherParameter(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode").param("content", "test").with(httpBasic("user","password")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
    }
    @Test
    void testWithContent_withContentAndType(@Autowired MockMvc mockMvc) throws Exception { // Consider renaming test method
        mockMvc.perform(get("/api/qrcode")
                        .param("content", "test")
                        .param("type", "jpg")
                        .with(httpBasic("user","password"))) // <-- Use lowercase 't'
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE));
    }

    @Test
    void testWithContent_andWrongType(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode")
                        .param("content", "test")
                        .param("Type", "bmp")
                        .with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Validation failed: type: Only png, jpeg, jpg and gif")));
    }

    @Test
    void testWithContent_andWrongLevel(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode")
                .param("content", "test")
                .param("correctionLevel", "X")
                        .with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Validation failed: correctionLevel: Permitted error correction levels are L, M, Q, H")));
    }
    @Test
    void testWithContent_andWrongSizeBelow150(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode")
                        .param("content", "test")
                        .param("Size", "50")
                        .with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Validation failed: size: Image size must be at least 150 pixels")));
    }

    @Test
    void testWithContent_andWrongSizeAbove350(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode")
                        .param("content", "test")
                        .param("Size", "400")
                        .with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", containsString("Validation failed: size: Image size must not exceed 350 pixels")));
    }

    @Test
    void testWithContent_allParamsWrongExceptContent(@Autowired MockMvc mockMvc) throws Exception {
        mockMvc.perform(get("/api/qrcode")
                        .param("content", "test")
                        .param("type", "bmp")
                        .param("correctionLevel", "X")
                        .param("Size", "50")
                        .with(httpBasic("user","password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }



}

