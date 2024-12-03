package com.banking.banking_system.cache;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CacheMetrics {

    public CacheMetrics(MeterRegistry registry, CachingService cachingService) {
        // Expose cache size
        registry.gauge("cache.size", cachingService, CachingService::getCacheSize);

        // Expose maximum cache size
        registry.gauge("cache.maxSize", cachingService, CachingService::getMaxSize);
    }
}