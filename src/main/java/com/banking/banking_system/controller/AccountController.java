package com.banking.banking_system.controller;

import com.banking.banking_system.cache.CachingService;
import com.banking.banking_system.model.Account;
import com.banking.banking_system.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final CachingService cachingService;

    @Autowired
    public AccountController(AccountService accountService, PasswordEncoder passwordEncoder, CachingService cachingService) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.cachingService = cachingService;
    }

    // Endpoint to clear all cache
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCache() {
        cachingService.clearCache();
        return ResponseEntity.ok("Cache cleared successfully.");
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountService.createAccount(account);
    }

    @PutMapping("/{id}/updatePassword")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordUpdateRequest) {
        String newPassword = passwordUpdateRequest.get("newPassword");
        accountService.updatePassword(id, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping("/{id}")
    public Optional<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        return accountService.updateAccount(id, updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted successfully.");
    }
}