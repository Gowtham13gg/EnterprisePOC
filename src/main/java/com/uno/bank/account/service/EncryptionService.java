package com.uno.bank.account.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.MessageDigest;

@Service
public class EncryptionService {

    @Value("${encryption.privateKey}")
    private String privateKeyString;

    @Value("${encryption.publicKey}")
    private String publicKeyString;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public void init() throws Exception {
        // Check if we need to generate keys
        if (privateKeyString == null || privateKeyString.equals("default_key_placeholder") ||
            publicKeyString == null || publicKeyString.equals("default_key_placeholder")) {
            // Generate new keys
            KeyPair keyPair = generateKeyPair();
            privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            
            System.out.println("Generated new keys for testing:");
            System.out.println("Private Key: " + privateKeyString);
            System.out.println("Public Key: " + publicKeyString);
        }
        
        try {
            // Initialize keys from properties
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            // Decode private key
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
            
            // Decode public key
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
            
            System.out.println("Successfully initialized encryption keys");
        } catch (Exception e) {
            System.err.println("Error initializing keys: " + e.getMessage());
            throw e;
        }
    }

    public String encrypt(String data) throws Exception {
        try {
            // First, create a SHA-256 hash of the data for verification
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            String dataHash = Base64.getEncoder().encodeToString(hashBytes);
            
            // For larger data, we'll use a hybrid approach:
            // 1. Generate a random AES key
            // 2. Encrypt the data with AES
            // 3. Encrypt the AES key with RSA
            // 4. Return the encrypted key + encrypted data
            
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
            
            String result = encryptedKeyStr + ":" + dataHash + ":" + encryptedDataStr;
            System.out.println("Successfully encrypted data with SHA-256 hash: " + dataHash);
            
            return result;
        } catch (Exception e) {
            System.err.println("Error in encryption: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public String decrypt(String encryptedPackage) throws Exception {
        try {
            System.out.println("Attempting to decrypt: " + encryptedPackage);
            
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
            
            System.out.println("Successfully decrypted data with SHA-256 hash: " + calculatedHash);
            return decryptedData;
        } catch (Exception e) {
            System.err.println("Error in decryption: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Generate key pair for initial setup
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
