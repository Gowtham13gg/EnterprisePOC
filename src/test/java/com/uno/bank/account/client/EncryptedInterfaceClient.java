package com.uno.bank.account.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uno.bank.account.model.DecryptedRequest;
import com.uno.bank.account.model.EncryptedRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for testing the encrypted interface service
 */
public class EncryptedInterfaceClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public EncryptedInterfaceClient(String baseUrl, String publicKeyString, String privateKeyString) throws Exception {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        
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
    public String sendRequest(String interfaceId, Map<String, Object> payload, Map<String, String> urlParams) throws Exception {
        // Create the request object
        DecryptedRequest request = new DecryptedRequest();
        request.setInterfaceId(interfaceId);
        request.setPayload(payload != null ? payload : new HashMap<>());
        request.setUrlparams(urlParams != null ? urlParams : new HashMap<>());
        
        // Convert to JSON
        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("Original request: " + requestJson);
        
        // Encrypt the request
        String encryptedData = encrypt(requestJson);
        System.out.println("Encrypted request: " + encryptedData);
        
        // Create the encrypted request object
        EncryptedRequest encryptedRequest = new EncryptedRequest();
        encryptedRequest.setEncryptedData(encryptedData);
        
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create the HTTP entity
        HttpEntity<EncryptedRequest> entity = new HttpEntity<>(encryptedRequest, headers);
        
        // Send the request
        String encryptedResponse = restTemplate.postForObject(baseUrl + "/api/interface/process", entity, String.class);
        System.out.println("Encrypted response: " + encryptedResponse);
        
        // Decrypt the response
        String decryptedResponse = decrypt(encryptedResponse);
        System.out.println("Decrypted response: " + decryptedResponse);
        
        return decryptedResponse;
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) throws Exception {
        // Replace with your actual keys
        String publicKey = System.getenv("ENCRYPTION_PUBLIC_KEY");
        String privateKey = System.getenv("ENCRYPTION_PRIVATE_KEY");
        
        if (publicKey == null || privateKey == null) {
            System.out.println("Please set ENCRYPTION_PUBLIC_KEY and ENCRYPTION_PRIVATE_KEY environment variables");
            System.exit(1);
        }
        
        EncryptedInterfaceClient client = new EncryptedInterfaceClient("http://localhost:8080", publicKey, privateKey);
        
        // Test getaccountdetails interface
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> urlParams = new HashMap<>();
        
        String response = client.sendRequest("getaccountdetails", payload, urlParams);
        System.out.println("Final response: " + response);
    }
}
