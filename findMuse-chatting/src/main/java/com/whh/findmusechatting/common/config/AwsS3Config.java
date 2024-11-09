package com.whh.findmusechatting.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.time.Duration;

@Configuration
public class AwsS3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient())
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider())
                .serviceConfiguration(s3Configuration())
                .build();
    }

    private SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
}
