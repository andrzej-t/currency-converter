package com.converter.currencyconverterback.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("currencies");
    }

    @CacheEvict(value = "currencies", allEntries = true)
    @Scheduled(fixedRateString = "${cache.currencies.ttl:3600000}")
    public void evictCurrenciesCache() {
    }
}
