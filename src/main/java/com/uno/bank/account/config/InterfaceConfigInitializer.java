package com.uno.bank.account.config;

import com.uno.bank.account.model.InterfaceConfig;
import com.uno.bank.account.repository.InterfaceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterfaceConfigInitializer {

    @Bean
    CommandLineRunner initInterfaceConfig(InterfaceConfigRepository repository) {
        return args -> {
            // Sample interface configuration for account details
            InterfaceConfig accountDetailsConfig = new InterfaceConfig();
            accountDetailsConfig.setInterfaceId("getaccountdetails");
            accountDetailsConfig.setEndpoint("http://localhost:8080/api/accounts");
            accountDetailsConfig.setMethodName("GET");
            accountDetailsConfig.setHasUriParams(false);
            repository.save(accountDetailsConfig);
            
            // Sample interface configuration for account by ID
            InterfaceConfig accountByIdConfig = new InterfaceConfig();
            accountByIdConfig.setInterfaceId("getaccountbyid");
            accountByIdConfig.setEndpoint("http://localhost:8080/api/accounts/{id}");
            accountByIdConfig.setMethodName("GET");
            accountByIdConfig.setHasUriParams(true);
            repository.save(accountByIdConfig);
            
            // Sample interface configuration for account by number
            InterfaceConfig accountByNumberConfig = new InterfaceConfig();
            accountByNumberConfig.setInterfaceId("getaccountbynumber");
            accountByNumberConfig.setEndpoint("http://localhost:8080/api/accounts/number/{accountNumber}");
            accountByNumberConfig.setMethodName("GET");
            accountByNumberConfig.setHasUriParams(true);
            repository.save(accountByNumberConfig);
        };
    }
}
