package com.uno.bank.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uno.bank.account.model.DecryptedRequest;
import com.uno.bank.account.model.InterfaceConfig;
import com.uno.bank.account.repository.InterfaceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class InterfaceService {

    @Autowired
    private InterfaceConfigRepository interfaceConfigRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    public String processRequest(String encryptedData) throws Exception {
        // Decrypt the request
        String decryptedData = encryptionService.decrypt(encryptedData);
        
        // Parse the decrypted JSON
        System.out.println("Decrypted data: " + decryptedData);
        DecryptedRequest request = objectMapper.readValue(decryptedData, DecryptedRequest.class);
        System.out.println("Parsed request: " + request);
        
        // Get interface configuration from database
        InterfaceConfig config = interfaceConfigRepository.findByInterfaceId(request.getInterfaceId());
        if (config == null) {
            throw new IllegalArgumentException("Interface ID not found: " + request.getInterfaceId());
        }
        
        // Build the URL with parameters if needed
        String url = config.getEndpoint();
        if (config.getHasUriParams() && request.getUrlparams() != null) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            for (Map.Entry<String, String> entry : request.getUrlparams().entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }
            url = builder.toUriString();
        }
        
        // Prepare headers and request entity
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request.getPayload(), headers);
        
        // Invoke the endpoint with the appropriate HTTP method
        HttpMethod method = HttpMethod.valueOf(config.getMethodName());
        ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
        
        // Encrypt the response
        return encryptionService.encrypt(response.getBody());
    }
}
