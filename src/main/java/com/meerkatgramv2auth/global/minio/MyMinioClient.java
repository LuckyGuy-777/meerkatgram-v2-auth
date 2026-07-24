package com.meerkatgramv2auth.global.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyMinioClient {
    @Bean
    public MinioClient minioClient(MinioConfig minioConfig) {
        return MinioClient.builder()
                .endpoint(minioConfig.minioEndpoint())
                .credentials(minioConfig.minioAccessKey(), minioConfig.minioSecretKey())
                .build();
    }
}

//  @Configuration  인스턴스를 개발자가 생성 못하도록 막아둔것.
// 이 안에 메소드를 @bean 으로 하나하나 등록하게 하는것
