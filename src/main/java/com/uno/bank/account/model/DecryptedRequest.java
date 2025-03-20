package com.uno.bank.account.model;

import lombok.Data;
import java.util.Map;

@Data
public class DecryptedRequest {
    private String interfaceId;
    private Map<String, Object> payload;
    private Map<String, String> urlparams;
}
