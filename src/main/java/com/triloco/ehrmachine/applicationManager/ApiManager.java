package com.triloco.ehrmachine.applicationManager;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
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
        String fullUrl = baseUrl + path;

        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            logger.info("GET Request: {}", fullUrl);
            logger.debug("Request Headers: {}", headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);
                logger.info("GET Response Status: {}", response.getStatusCode());
                logger.debug("GET Response Body: {}", response.getBody());
                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                handleHttpException("GET", fullUrl, ex);
                throw new Exception("GET failed: " + extractErrorBody(ex), ex);
            }
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            logger.error("GET request failed after retries", ex);
            throw ex;
        }
    }

    public <T> String post(String path, T requestBody) throws Exception {
        String fullUrl = baseUrl + path;

        Supplier<String> supplier = Retry.decorateSupplier(retry, () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
            }

            HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);

            logger.info("POST Request: {}", fullUrl);
            logger.debug("Request Headers: {}", headers);
            logger.debug("Request Body: {}", requestBody);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);
                logger.info("POST Response Status: {}", response.getStatusCode());
                logger.debug("POST Response Body: {}", response.getBody());
                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                handleHttpException("POST", fullUrl, ex);
                throw new Exception("POST failed: " + extractErrorBody(ex), ex);
            }
        });

        try {
            return supplier.get();
        } catch (Exception ex) {
            logger.error("POST request failed after retries", ex);
            throw ex;
        }
    }

    // ---------- Helper Methods ----------

    private void handleHttpException(String method, String url, HttpStatusCodeException ex) {
        logger.error("{} Request to {} failed with status: {}", method, url, ex.getStatusCode());
        logger.error("Response Headers: {}", ex.getResponseHeaders());

        // Try to decode the response body
        String errorBody = "";
        try {
            byte[] rawBytes = ex.getResponseBodyAsByteArray();
            if (rawBytes != null && rawBytes.length > 0) {
                errorBody = new String(rawBytes, StandardCharsets.UTF_8);
                logger.error("Decoded Error Body: {}", errorBody);
            } else {
                logger.warn("Error response body is empty");
            }
        } catch (Exception decodeEx) {
            logger.error("Failed to decode error body", decodeEx);
        }

        // Optional: Try parsing JSON
        try {
            if (errorBody != null && !errorBody.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(errorBody);
                String dataField = root.path("data").asText(null);
                String language = root.path("language").asText(null);
                logger.error("Parsed 'data': {}", dataField);
                logger.error("Parsed 'language': {}", language);
            }
        } catch (Exception parseEx) {
            logger.warn("Error body is not valid JSON or missing fields", parseEx);
        }
    }

    private String extractErrorBody(HttpStatusCodeException ex) {
        try {
            byte[] raw = ex.getResponseBodyAsByteArray();
            return (raw != null && raw.length > 0) ? new String(raw, StandardCharsets.UTF_8) : "";
        } catch (Exception e) {
            logger.error("Failed to extract error body", e);
            return "";
        }
    }
}
