package com.triloco.ehrmachine.applicationManager;


import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Duration;
import java.util.function.Supplier;

public class ApiManager {

    private static final ApiManager instance = new ApiManager();
    private final RestTemplate restTemplate;
    private final Retry retry;

    private String authToken;

    private ApiManager() {
        this.restTemplate = new RestTemplate();

        // Configure retry: 3 attempts with 500ms wait between tries
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(HttpStatusCodeException.class)
                .build();

        retry = Retry.of("apiRetry", config);
    }

    public static ApiManager getInstance() {
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    // Generic GET method
    public <T> T get(String url, Class<T> responseType) throws Exception {
        Supplier<T> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);

            return response.getBody();
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            throw new Exception("GET request failed after retries", ex);
        }
    }

    // Generic POST method with JSON payload
    public <T, R> R post(String url, T requestBody, Class<R> responseType) throws Exception {
        Supplier<R> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }

            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<R> response = restTemplate.postForEntity(url, entity, responseType);
            return response.getBody();
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            throw new Exception("POST request failed after retries", ex);
        }
    }
}
