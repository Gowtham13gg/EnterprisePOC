package com.uno.bank.account.config;

import com.uno.bank.account.service.EncryptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.security.KeyPair;
import java.util.Base64;

@Configuration
public class EncryptionConfig {

    @Bean
    @DependsOn("encryptionService")
    public String initializeEncryptionService(EncryptionService encryptionService) throws Exception {
        try {
            encryptionService.init();
        } catch (Exception e) {
            System.err.println("Error initializing encryption service: " + e.getMessage());
            // Generate new keys if initialization fails
            KeyPair keyPair = EncryptionService.generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            
            System.out.println("Generated new keys for testing:");
            System.out.println("Private Key: " + privateKey);
            System.out.println("Public Key: " + publicKey);
            
            // Set environment variables for testing
            System.setProperty("ENCRYPTION_PRIVATE_KEY", privateKey);
            System.setProperty("ENCRYPTION_PUBLIC_KEY", publicKey);
            
            // Try initialization again with new keys
            encryptionService.init();
        }
        return "encryptionServiceInitialized";
    }
    
    // This method can be used to generate new key pairs if needed
    public static void generateAndPrintKeys() throws Exception {
        KeyPair keyPair = EncryptionService.generateKeyPair();
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        
        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key: " + publicKey);
    }
    
    // Main method to generate keys from command line
    public static void main(String[] args) throws Exception {
        generateAndPrintKeys();
    }
}
