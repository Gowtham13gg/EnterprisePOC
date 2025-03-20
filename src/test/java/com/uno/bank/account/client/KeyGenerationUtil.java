package com.uno.bank.account.client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * Utility class to generate RSA key pairs for testing
 */
public class KeyGenerationUtil {
    
    public static void main(String[] args) throws Exception {
        // Generate a new key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        // Encode keys to Base64
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        
        // Print keys
        System.out.println("Private Key:");
        System.out.println(privateKey);
        System.out.println("\nPublic Key:");
        System.out.println(publicKey);
        
        // Print environment variable export commands
        System.out.println("\nExport commands for environment variables:");
        System.out.println("export ENCRYPTION_PRIVATE_KEY=\"" + privateKey + "\"");
        System.out.println("export ENCRYPTION_PUBLIC_KEY=\"" + publicKey + "\"");
    }
}
