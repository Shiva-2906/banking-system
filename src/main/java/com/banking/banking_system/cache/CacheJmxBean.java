package com.banking.banking_system.cache;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "com.banking.banking_system.cache:type=CachingService")
public class CacheJmxBean {

    private final CachingService cachingService;

    public CacheJmxBean(CachingService cachingService) {
        this.cachingService = cachingService;
    }

    @ManagedAttribute(description = "Current cache size")
    public int getCacheSize() {
        return cachingService.getCacheSize();
    }

    @ManagedAttribute(description = "Maximum cache size")
    public int getMaxSize() {
        return cachingService.getMaxSize();
    }

    @ManagedAttribute(description = "Clear cache")
    public void clearCache() {
        cachingService.clear();
    }
}