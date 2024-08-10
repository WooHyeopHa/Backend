package com.whh.findmuseapi.common.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class SshDataSourceConfig {

    private final SshTunnelConfig initializer;

    @Bean("dataSource")
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        System.out.println("시작");
        log.info("Datasource Properties URL: {}", properties.getUrl());
        log.info("Datasource Properties Username: {}", properties.getUsername());
        log.info("Datasource Properties Password: {}", properties.getPassword());
        log.info("Datasource Properties Driver ClassName: {}", properties.getDriverClassName());
        Integer forwardedPort = initializer.buildSshConnection();
        String url = properties.getUrl().replace("[forwardedPort]", String.valueOf(forwardedPort));
        log.info(url);
        return DataSourceBuilder.create()
            .url(url)
            .username(properties.getUsername())
            .password(properties.getPassword())
            .driverClassName(properties.getDriverClassName())
            .build();
    }
}
