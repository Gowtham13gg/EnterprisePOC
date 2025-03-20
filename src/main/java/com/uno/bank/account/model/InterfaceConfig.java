package com.uno.bank.account.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Data
public class InterfaceConfig {
    @Id
    private String interfaceId;
    
    @Column(nullable = false)
    private String endpoint;
    
    @Column(nullable = false)
    private String methodName;
    
    @Column(nullable = false)
    private Boolean hasUriParams;
}
