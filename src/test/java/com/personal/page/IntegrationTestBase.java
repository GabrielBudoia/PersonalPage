package com.personal.page;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "DB_PASSWORD=test",
                "STRIPE_SECRET_KEY=sk_test_dummy",
                "STRIPE_WEBHOOK_SECRET=whsec_test_dummy",
                "ADMIN_PASSWORD=test-password"
        })
@Import(IntegrationTestBase.Containers.class)
public abstract class IntegrationTestBase {

    @TestConfiguration(proxyBeanMethods = false)
    static class Containers {

        // Same image as docker-compose: tests run against the real database engine
        @Bean
        @ServiceConnection
        PostgreSQLContainer postgres() {
            return new PostgreSQLContainer("postgres:16");
        }
    }
}