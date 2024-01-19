package com.spom.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestSpomarketsServiceApplication {

    @Bean
    @ServiceConnection
    GenericContainer<?> mongoContainer() {
        return new GenericContainer<>(DockerImageName.parse("mongo:latest"))
            .withExposedPorts(11111); // MongoDB default port
    }

    public static void main(String[] args) {
        SpringApplication.from(SpomarketsServiceApplication::main)
            .with(TestSpomarketsServiceApplication.class)
            .run(args);
    }
}
