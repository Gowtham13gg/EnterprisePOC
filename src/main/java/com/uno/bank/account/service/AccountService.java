package com.uno.bank.account.service;

import com.uno.bank.account.model.Account;
import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    Account getAccountById(Long id);
    Account getAccountByNumber(String accountNumber);
}
