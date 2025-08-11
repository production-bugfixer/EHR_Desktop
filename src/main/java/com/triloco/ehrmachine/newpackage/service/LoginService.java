package com.triloco.ehrmachine.newpackage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.triloco.ehrmachine.applicationManager.ApiManager;
import com.triloco.ehrmachine.applicationManager.utils.ObjectEncryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triloco.ehrmachine.applicationManager.CacheManager;
import com.triloco.ehrmachine.applicationManager.model.ApiResponse;

import java.util.HashMap;
import java.util.Map;

public class LoginService {

    private static LoginService instance;
    private static final ApiManager apiManager = ApiManager.getInstance();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private LoginService() {
    }

    public static synchronized LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public Boolean login(String username, String password) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userType", "DOCTOR");
        payload.put("username", username);
        payload.put("email", username);
        payload.put("password", password);
        payload.put("phoneNumber", "");

        Map<String, String> encryptedObject = ObjectEncryptor.encryptMap(payload);

        try {
            String responseJson = apiManager.post("/authenticate/auth/v1/doctor", encryptedObject);

            // Parse the JSON to extract the 'body'
            Map<String, Object> responseMap = objectMapper.readValue(responseJson, Map.class);

            String encodedBody = (String) responseMap.get("data");

            if (encodedBody == null) {
                System.getLogger(LoginService.class.getName()).log(System.Logger.Level.WARNING, "No 'body' field in response");
                return null;
            }

            // Decrypt the encoded body
            String decryptedBody = ObjectEncryptor.decrypt(encodedBody);
            ApiResponse apiResponse = objectMapper.readValue(decryptedBody, ApiResponse.class);
            JsonNode dataNode = apiResponse.getData();

if (dataNode.isTextual()) {
    String dataAsString = dataNode.asText();
    //System.out.println("Data as string: " + dataAsString);
    CacheManager.put("user-token", dataAsString);
    return true;
} 
            
            return false;

        } catch (Exception ex) {
            System.getLogger(LoginService.class.getName()).log(System.Logger.Level.ERROR, "Login failed", ex);
            return false;
        }
    }
}
