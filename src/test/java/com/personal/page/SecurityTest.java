package com.personal.page;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityTest extends IntegrationTestBase {

    @Value("${local.server.port}")
    int port;

    // Java's built-in client; it does not follow redirects by default
    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void adminRedirectsToLoginWhenAnonymous() throws Exception {
        HttpResponse<String> res = send(HttpRequest.newBuilder(url("/admin")).GET());

        assertThat(res.statusCode()).isEqualTo(302);
        assertThat(res.headers().firstValue("Location").orElseThrow()).endsWith("/login");
    }

    @Test
    void webhookSkipsCsrfButStillVerifiesSignature() throws Exception {
        HttpResponse<String> res = send(HttpRequest.newBuilder(url("/webhooks/stripe"))
                .header("Content-Type", "application/json")
                .header("Stripe-Signature", "t=1,v1=invalid")
                .POST(HttpRequest.BodyPublishers.ofString("{}")));

        // 400 = reached the controller and the signature check refused it (403 would mean CSRF blocked it)
        assertThat(res.statusCode()).isEqualTo(400);
    }

    @Test
    void checkoutWithoutCsrfTokenIsRejected() throws Exception {
        HttpResponse<String> res = send(HttpRequest.newBuilder(url("/payments/checkout"))
                .POST(HttpRequest.BodyPublishers.noBody()));

        assertThat(res.statusCode()).isEqualTo(403);
    }

    private URI url(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private HttpResponse<String> send(HttpRequest.Builder request) throws Exception {
        return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}