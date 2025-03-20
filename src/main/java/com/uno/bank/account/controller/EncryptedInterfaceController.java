package com.uno.bank.account.controller;

import com.uno.bank.account.model.EncryptedRequest;
import com.uno.bank.account.service.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interface")
public class EncryptedInterfaceController {

    @Autowired
    private InterfaceService interfaceService;

    @PostMapping("/process")
    public ResponseEntity<String> processEncryptedRequest(@RequestBody EncryptedRequest request) {
        try {
            System.out.println("Received encrypted request: " + request.getEncryptedData());
            String encryptedResponse = interfaceService.processRequest(request.getEncryptedData());
            System.out.println("Sending encrypted response: " + encryptedResponse);
            return ResponseEntity.ok(encryptedResponse);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error processing request: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }
}
