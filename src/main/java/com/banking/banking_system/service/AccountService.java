package com.banking.banking_system.service;

import com.banking.banking_system.model.Account;
import com.banking.banking_system.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    @Cacheable(value = "accounts", key = "#id")
    public Optional<Account> getAccountById(Long id) {
        System.out.println("Fetching from database for ID: " + id);
        return accountRepository.findById(id);
    }


    public Account updateAccount(Long id, Account updatedAccount) {
        return accountRepository.findById(id).map(account -> {
            account.setAccountHolderName(updatedAccount.getAccountHolderName());
            account.setAccountType(updatedAccount.getAccountType());
            account.setBalance(updatedAccount.getBalance());
            account.setUsername(updatedAccount.getUsername());
            return accountRepository.save(account);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @CacheEvict(value = "accounts", key = "#id")
    public void updatePassword(Long id, String newPassword) {
        accountRepository.findById(id).ifPresent(account -> {
            account.setPassword(newPassword);
            accountRepository.save(account);
        });
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public void clearCache() {
        System.out.println("Cache cleared");
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}