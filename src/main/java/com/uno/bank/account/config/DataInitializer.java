package com.uno.bank.account.config;

import com.uno.bank.account.model.Account;
import com.uno.bank.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AccountRepository repository) {
        return args -> {
            Account account1 = new Account();
            account1.setAccountNumber("1001001001");
            account1.setAccountHolderName("John Doe");
            account1.setAccountType("SAVINGS");
            account1.setBalance(5000.0);
            account1.setStatus("ACTIVE");
            account1.setEmploymentStatus("EMPLOYED");
            account1.setCompany("Tech Corp");
            account1.setSalary(75000.0);
            repository.save(account1);

            Account account2 = new Account();
            account2.setAccountNumber("1001001002");
            account2.setAccountHolderName("Jane Smith");
            account2.setAccountType("CURRENT");
            account2.setBalance(10000.0);
            account2.setStatus("ACTIVE");
            account2.setEmploymentStatus("SELF_EMPLOYED");
            account2.setCompany("Smith Consulting");
            account2.setSalary(120000.0);
            repository.save(account2);
        };
    }
}
