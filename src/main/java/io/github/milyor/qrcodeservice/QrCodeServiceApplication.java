package io.github.milyor.qrcodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QrCodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrCodeServiceApplication.class, args);
    }

}
