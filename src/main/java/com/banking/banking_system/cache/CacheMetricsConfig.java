package com.banking.banking_system.cache;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheMetricsConfig {

    private final CachingService cachingService;

    @Autowired
    public CacheMetricsConfig(CachingService cachingService, MeterRegistry meterRegistry) {
        this.cachingService = cachingService;

        // Register custom metrics
        meterRegistry.gauge("cache.size", cachingService, CachingService::getCacheSize);
        meterRegistry.gauge("cache.maxSize", cachingService, CachingService::getMaxSize);
    }
}