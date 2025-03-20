package com.uno.bank.account.client;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Simple test client for the encrypted interface service
 * Uses java.net.HttpURLConnection instead of Spring's RestTemplate
 */
public class SimpleInterfaceTest {

    private final String baseUrl;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public SimpleInterfaceTest(String baseUrl, String publicKeyString, String privateKeyString) throws Exception {
        this.baseUrl = baseUrl;
        
        // Initialize keys
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        // Decode private key
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);
        
        // Decode public key
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Encrypt data using the public key
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypt data using the private key
     */
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    /**
     * Send an encrypted request to the interface service
     */
    public String sendRequest(String interfaceId) throws Exception {
        // Create the request JSON - note the field names match DecryptedRequest.java
        String requestJson = String.format(
            "{\"interfaceId\":\"%s\",\"payload\":{},\"urlparams\":{}}", 
            interfaceId
        );
        System.out.println("Original request: " + requestJson);
        
        // Encrypt the request
        String encryptedData = encrypt(requestJson);
        System.out.println("Encrypted request: " + encryptedData);
        
        // Create the encrypted request JSON
        String encryptedRequestJson = String.format("{\"encryptedData\":\"%s\"}", encryptedData);
        
        System.out.println("Sending request: " + encryptedRequestJson);
        
        // Set up the HTTP connection
        URL url = new URL(baseUrl + "/api/interface/process");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = encryptedRequestJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // Get the response
        int responseCode = connection.getResponseCode();
        System.out.println("Response code: " + responseCode);
        
        if (responseCode == 200) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            
            String encryptedResponse = response.toString();
            System.out.println("Encrypted response: " + encryptedResponse);
            
            // Decrypt the response
            String decryptedResponse = decrypt(encryptedResponse);
            System.out.println("Decrypted response: " + decryptedResponse);
            
            return decryptedResponse;
        } else {
            // Read error response
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
            }
            System.out.println("Error response: " + errorResponse.toString());
            throw new IOException("Server returned HTTP response code: " + responseCode + 
                                  " for URL: " + url.toString());
        }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        try {
            // Get keys from environment variables
            String publicKey = System.getenv("ENCRYPTION_PUBLIC_KEY");
            String privateKey = System.getenv("ENCRYPTION_PRIVATE_KEY");
            
            if (publicKey == null || privateKey == null) {
                System.out.println("Please set ENCRYPTION_PUBLIC_KEY and ENCRYPTION_PRIVATE_KEY environment variables");
                System.exit(1);
            }
            
            // Create test client
            SimpleInterfaceTest client = new SimpleInterfaceTest("http://localhost:8080", publicKey, privateKey);
            
            // Test getaccountdetails interface
            String response = client.sendRequest("getaccountdetails");
            System.out.println("Final response: " + response);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
