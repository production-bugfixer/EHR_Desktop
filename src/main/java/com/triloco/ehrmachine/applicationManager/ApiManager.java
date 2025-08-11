package com.triloco.ehrmachine.applicationManager;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

public class ApiManager {

    private static final Logger logger = LoggerFactory.getLogger(ApiManager.class);
    private static final ApiManager instance = new ApiManager();

    private final RestTemplate restTemplate;
    private final Retry retry;
    private final String baseUrl;
    private String authToken;

    private ApiManager() {
        this.restTemplate = new RestTemplate();
        this.baseUrl = PropertiesLoader.get("baseUrl");

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(HttpStatusCodeException.class)
                .build();

        this.retry = Retry.of("apiRetry", config);
    }

    public static ApiManager getInstance() {
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String get(String path) throws Exception {
        //String fullUrl = baseUrl + path;
 String fullUrl = "http://147.79.66.20:9090/authenticate/auth/v1/doctor";
        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Log request
            logger.info("GET Request: {}", fullUrl);
            logger.debug("Request Headers: {}", headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);

                // Log response
                logger.info("GET Response Status: {}", response.getStatusCode());
                logger.debug("GET Response Body: {}", response.getBody());

                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                logger.error("GET Request failed with status: {}", ex.getStatusCode());
                logger.error("Response Body: {}", ex.getResponseBodyAsString());
                throw ex;
            }
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            logger.error("GET request failed after retries", ex);
            throw new Exception("GET request failed after retries", ex);
        }
    }

    public <T> String post(String path, T requestBody) throws Exception {
        //String fullUrl = baseUrl + path;
 String fullUrl = "http://147.79.66.20:9090/authenticate/auth/v1/doctor";
        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }
            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

            // Log request
            logger.info("POST Request: {}", fullUrl);
            logger.debug("Request Headers: {}", headers);
            logger.debug("Request Body: {}", requestBody);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);

                // Log response
                logger.info("POST Response Status: {}", response.getStatusCode());
                logger.debug("POST Response Body: {}", response.getBody());

                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                logger.error("POST Request failed with status: {}", ex.getStatusCode());
                logger.error("Response Body: {}", ex.getResponseBodyAsString());
                throw ex;
            }
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            logger.error("POST request failed after retries", ex);
            throw new Exception("POST request failed after retries", ex);
        }
    }
}

   
