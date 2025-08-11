package com.triloco.ehrmachine.applicationManager.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ObjectEncryptor {

    private static final String SECRET_KEY = "1234567890123456"; // 16 chars (AES-128)
    private static final String INIT_VECTOR = "6543210987654321"; // 16 chars
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Encrypts a Map<String, Object> to a Base64 AES string.
     */
    public static Map<String,String> encryptMap(Map<String, Object> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            return encrypt(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt map", e);
        }
    }

    /**
     * Decrypts a Base64 AES string back to a Map<String, Object>.
     */
    public static Map<String, Object> decryptToMap(String encryptedData) {
        try {
            String json = decrypt(encryptedData);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt map", e);
        }
    }

    private static Map<String,String> encrypt(String plainText) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            Map<String,String> map=new HashMap<>();
            map.put("data",Base64.getEncoder().encodeToString(encrypted));
            return map;
        } catch (Exception ex) {
            throw new RuntimeException("Error while encrypting", ex);
        }
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException("Error while decrypting", ex);
        }
    }
}
