package com.example.gitscorer.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CacheConfiguration {
    @Bean
    public CacheManager getTestCacheManager() {
        return new NoOpCacheManager();
    }
}
