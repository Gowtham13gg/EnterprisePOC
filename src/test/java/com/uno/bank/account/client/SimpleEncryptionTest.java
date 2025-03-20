package com.uno.bank.account.client;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Simple test for encryption/decryption using RSA
 */
public class SimpleEncryptionTest {

    private static final String TEST_DATA = "{\"interfaceId\":\"getaccountdetails\",\"payload\":{},\"urlParams\":{}}";
    
    private PublicKey publicKey;
    private PrivateKey privateKey;
    
    public SimpleEncryptionTest(String publicKeyString, String privateKeyString) throws Exception {
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
     * Test encryption and decryption
     */
    public void testEncryptionDecryption() throws Exception {
        System.out.println("Original data: " + TEST_DATA);
        
        // Encrypt
        String encryptedData = encrypt(TEST_DATA);
        System.out.println("Encrypted data: " + encryptedData);
        
        // Decrypt
        String decryptedData = decrypt(encryptedData);
        System.out.println("Decrypted data: " + decryptedData);
        
        // Verify
        if (TEST_DATA.equals(decryptedData)) {
            System.out.println("Test PASSED: Decrypted data matches original data");
        } else {
            System.out.println("Test FAILED: Decrypted data does not match original data");
        }
        
        // Print algorithm details
        Cipher cipher = Cipher.getInstance("RSA");
        System.out.println("Encryption algorithm: " + cipher.getAlgorithm());
        System.out.println("Provider: " + cipher.getProvider());
        System.out.println("Key size: " + publicKey.getEncoded().length * 8 + " bits");
    }
    
    /**
     * Generate a new key pair
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        try {
            // Get keys from environment variables
            String publicKeyString = System.getenv("ENCRYPTION_PUBLIC_KEY");
            String privateKeyString = System.getenv("ENCRYPTION_PRIVATE_KEY");
            
            if (publicKeyString == null || privateKeyString == null) {
                System.out.println("Please set ENCRYPTION_PUBLIC_KEY and ENCRYPTION_PRIVATE_KEY environment variables");
                System.exit(1);
            }
            
            // Create test instance
            SimpleEncryptionTest test = new SimpleEncryptionTest(publicKeyString, privateKeyString);
            
            // Run test
            test.testEncryptionDecryption();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
