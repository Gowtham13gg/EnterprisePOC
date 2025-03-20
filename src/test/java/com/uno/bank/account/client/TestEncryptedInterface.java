package com.uno.bank.account.client;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Test client for the encrypted interface service
 * Uses hybrid encryption (RSA + AES) with SHA-256 hash verification
 */
public class TestEncryptedInterface {

    public static void main(String[] args) {
        try {
            // Get the newly generated keys from KeyGenerationUtil
            String privateKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCh3O0ls6EgkYboItJCfDFPO8RGkF6RHVB9rKk2Q/rBXR8zRBv6WJDIjB4+/tQjhrvqw4JjYYzRp60sUspeyPOrV+g3phc3sDjas9lvJ+IVqyvHaxfcjG3YPYHbnveVqGrbU5RszQz51oyw4UhccGEnVerqyAoAt7GTZyAhsxJCGV1AGZZ/ohs0SwvvwW3xTxpnIZY05RF9LQ+Yr2bqpKuVy4iPfjrREG0Dwz+cNu8UwefvrcgAJp/TtNLiZgt7SmS+0DywpqKgauKOz9put84aGlj1ltExNrM2AgXMc81yQ3mzkFN2pjiGujw5PvbedlGtIXECOEHxkCJ65kYlBDQDAgMBAAECggEALo8xqgQjJ46k2zZU++nkdzGuckvJ3/0qL5RQcjsYsLFcLa98SsVaFE31z1IW7Icvm8aURYkEbDRKxHKmIqC0MsfNFODSUeL/X62XMHms92Wu6KR+DyNX33lF1spB1otE/YTrr2ZGQsF7489RtMFguONmeRWEhxNP4aIq5HaxnFBhIyOSAVGxjffAz7c23fk5219J9jwFHPxNCBSDl2ynxj+AZKJ6oMzHt0z5UAqBGsHRANZNaeC2CKQOH/luW4C9kHpuqTT7xjt+VDntSUhGeevYzimzjdydIo52Dl5FBjkayPrNVHT2pWM5dH6Je8SqUbmkCk1iTAqVRvjHH5RDxQKBgQDYu4QL/I7JTIR34RpHT48NZqCkqMW41a5DeHBzQYkE4nzf75uixLbj5QGAm3xnBhwye59Q6+/qFpEfgxF1HROlLGrwd4BUqL/GBlT8+Ruwc9DOzanRFRKiYvgLCaPWEPGVbpUBOcZCb2IvCwBot6cHVAnM+uKKOfAYaJ7f/MKAtwKBgQC/MHRPLS4uu0AE1owsaFHjBMQJcpDCn9SmFW3QPQsRSG/mceDhGpiiUP/kgpaR9A8gG2/uUGsMOVNh8oczn41FcFLvmaVYEx+HylTxeSWDT+3FXSUfdUUO1SY7F0ZBO+7R6kmm+OyaSyqTJj72hZ88rnErPKZ2P/7O/5lfUl6DFQKBgQDLDSNxd3Tu81lIVwUfulHz5CqtGdHkkY1qePQhh0yv3uHPi9TschBHAs6dhw7OFtNkGJ3yWpmzXuEn/MvmRQ+auDqYf90jc9X32QbW2ywt7NO8bMKkuHF04fQxox5z9/veXHQNaettYp1CJbSEZ0t9eiaUfJixmAZxV9NBVrWDkQKBgGtM2Wn6VFn701EjzCW8IoEDkrYuqxnfs+vGSQw+xjBb9BlkPi53y8QFK6hmtfSSAc8mNuqMDZgdsDNfdwu89v9Kq/E2zPoiOWbvYj7nm+sXd711qjj+itRpQfyV8mL4LO94yv8/4yIxH2LYayvWEGlxCaiXcGEbR3mLL9u0kcCFAoGAFsExybJsL/FK+mwDN1gsCMc7Iblzup16O8uJp3Anh/thF/kIrK4KqJkhUmWbzukRj0E0lrtktxNulPyyUmRNiR/aT4DsgF97KxIW5JLL1KAeVYrDMFhF78ifLmZt66LJCmyOilijhvT9cPlWQdrLgAjg+z6CYqMOIFOKxs//COM=";
            String publicKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAodztJbOhIJGG6CLSQnwxTzvERpBekR1QfaypNkP6wV0fM0Qb+liQyIwePv7UI4a76sOCY2GM0aetLFLKXsjzq1foN6YXN7A42rPZbyfiFasrx2sX3Ixt2D2B2573lahq21OUbM0M+daMsOFIXHBhJ1Xq6sgKALexk2cgIbMSQhldQBmWf6IbNEsL78Ft8U8aZyGWNOURfS0PmK9m6qSrlcuIj3460RBtA8M/nDbvFMHn763IACaf07TS4mYLe0pkvtA8sKaioGrijs/abrfOGhpY9ZbRMTazNgIFzHPNckN5s5BTdqY4hro8OT723nZRrSFxAjhB8ZAieuZGJQQ0AwIDAQAB";

            // Initialize keys
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            // Decode private key
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            // Decode public key
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // Create test request
            String requestJson = "{\"interfaceId\":\"getaccountdetails\",\"payload\":{},\"urlparams\":{}}";
            System.out.println("Original request: " + requestJson);
            
            // Encrypt the request using hybrid encryption (RSA + AES)
            String encryptedData = hybridEncrypt(requestJson, publicKey);
            System.out.println("Encrypted request: " + encryptedData);
            
            // Create the encrypted request JSON
            String encryptedRequestJson = "{\"encryptedData\":\"" + encryptedData + "\"}";
            
            // Send the request to the server
            URL url = new URL("http://localhost:8080/api/interface/process");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
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
                String decryptedResponse = hybridDecrypt(encryptedResponse, privateKey);
                System.out.println("Decrypted response: " + decryptedResponse);
                
                System.out.println("Test PASSED: Successfully encrypted, sent, received, and decrypted data");
                System.out.println("Encryption algorithm: Hybrid RSA + AES with SHA-256 hash verification");
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
                System.out.println("Test FAILED: Could not process encrypted request");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Encrypt data using hybrid encryption (RSA + AES)
     * 1. Generate a random AES key
     * 2. Encrypt the data with AES
     * 3. Encrypt the AES key with RSA
     * 4. Return the encrypted key + encrypted data
     */
    private static String hybridEncrypt(String data, PublicKey publicKey) throws Exception {
        // Create SHA-256 hash of the data for verification
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes());
        String dataHash = Base64.getEncoder().encodeToString(hashBytes);
        
        // Generate a random AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();
        
        // Encrypt the data with AES
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedData = aesCipher.doFinal(data.getBytes());
        
        // Encrypt the AES key with RSA
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(aesKey.getEncoded());
        
        // Combine the encrypted key, hash, and data
        // Format: {base64(encryptedKey)}:{base64(dataHash)}:{base64(encryptedData)}
        String encryptedKeyStr = Base64.getEncoder().encodeToString(encryptedKey);
        String encryptedDataStr = Base64.getEncoder().encodeToString(encryptedData);
        
        return encryptedKeyStr + ":" + dataHash + ":" + encryptedDataStr;
    }
    
    /**
     * Decrypt data using hybrid encryption (RSA + AES)
     * 1. Split the package into its components
     * 2. Decrypt the AES key with RSA
     * 3. Decrypt the data with AES
     * 4. Verify the hash
     */
    private static String hybridDecrypt(String encryptedPackage, PrivateKey privateKey) throws Exception {
        // Split the package into its components
        String[] parts = encryptedPackage.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted package format");
        }
        
        String encryptedKeyStr = parts[0];
        String originalHash = parts[1];
        String encryptedDataStr = parts[2];
        
        // Decode the components
        byte[] encryptedKey = Base64.getDecoder().decode(encryptedKeyStr);
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataStr);
        
        // Decrypt the AES key with RSA
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        
        // Decrypt the data with AES
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = aesCipher.doFinal(encryptedData);
        String decryptedData = new String(decryptedBytes);
        
        // Verify the hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(decryptedData.getBytes());
        String calculatedHash = Base64.getEncoder().encodeToString(hashBytes);
        
        if (!calculatedHash.equals(originalHash)) {
            throw new SecurityException("Data integrity check failed: hash mismatch");
        }
        
        return decryptedData;
    }
}
