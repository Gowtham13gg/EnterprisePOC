package com.uno.bank.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private String privateKeyString;
    private String publicKeyString;

    @BeforeEach
    public void setup() throws Exception {
        // Generate test keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        
        privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        
        // Setup encryption service
        encryptionService = new EncryptionService();
        ReflectionTestUtils.setField(encryptionService, "privateKeyString", privateKeyString);
        ReflectionTestUtils.setField(encryptionService, "publicKeyString", publicKeyString);
        encryptionService.init();
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {
        // Test data
        String originalData = "{\"interfaceId\":\"getaccountdetails\",\"payload\":{},\"urlParams\":{}}";
        
        // Encrypt
        String encryptedData = encryptionService.encrypt(originalData);
        
        // Verify encrypted data is different from original
        assertNotEquals(originalData, encryptedData);
        
        // Decrypt
        String decryptedData = encryptionService.decrypt(encryptedData);
        
        // Verify decrypted data matches original
        assertEquals(originalData, decryptedData);
    }
}
