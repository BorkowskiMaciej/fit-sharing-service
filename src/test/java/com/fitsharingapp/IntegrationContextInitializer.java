package com.fitsharingapp;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@Testcontainers
public class IntegrationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("fit-sharing-db-test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer("mongo:latest");


    static {
        Startables.deepStart(postgresContainer, mongoContainer).join();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword(),
                "spring.data.mongodb.uri=" + mongoContainer.getReplicaSetUrl()
        ).applyTo(applicationContext.getEnvironment());
    }

}
