package com.banking.banking_system.cache;

import com.banking.banking_system.model.Account;
import com.banking.banking_system.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CachingService {

    private final AccountRepository accountRepository;
    private final int maxSize;
    private final Map<Long, Account> cache;
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);

    @Autowired
    public CachingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.maxSize = 100; // Configurable max size
        this.cache = new LinkedHashMap<Long, Account>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Account> eldest) {
                if (size() > maxSize) {
                    logger.info("Evicting account with ID {} to database", eldest.getKey());
                    accountRepository.save(eldest.getValue());
                    return true;
                }
                return false;
            }
        };
    }

    // Add Account to Cache
    public void add(Account account) {
        cache.put(account.getId(), account);
        logger.info("Added account with ID {} to cache", account.getId());
    }

    // Remove Account from Cache and Database
    public void remove(Long accountId) {
        cache.remove(accountId);
        accountRepository.deleteById(accountId);
        logger.info("Removed account with ID {} from cache and database", accountId);
    }

    // Remove All Accounts from Cache and Database
    public void removeAll() {
        cache.clear();
        accountRepository.deleteAll();
        logger.info("Removed all accounts from cache and database");
    }

    @Cacheable("accounts")
    public Optional<Account> get(Long accountId) {
        if (cache.containsKey(accountId)) {
            logger.info("Cache hit for account ID {}", accountId);
            return Optional.of(cache.get(accountId));
        }
        logger.info("Cache miss for account ID {}", accountId);
        Optional<Account> account = accountRepository.findById(accountId);
        account.ifPresent(this::add); // Add to cache if found in DB
        return account;
    }

    // Clear the Cache Only
    public void clear() {
        cache.clear();
        logger.info("Cleared the cache");
    }

    // Expose Cache Size for Monitoring
    public int getCacheSize() {
        return cache.size();
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public void clearCache() {
        logger.info("Cleared all entries from the cache");
    }

    // Expose Max Cache Size for Monitoring
    public int getMaxSize() {
        return maxSize;
    }
}