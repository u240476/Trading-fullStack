package com.example.cache;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class CachedStocks {

    private final Cache<String, Stock> cache =
            Caffeine.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(Duration.ofMinutes(1140))
                    .build();

    
    public void createEntry(String ticker, double[] prices) {

        Stock stock = new Stock.Builder(prices)
                .build();

        cache.put(ticker, stock);
    }

    
    public Stock getEntry(String ticker) {
        System.out.println("CACHE HIT: " + ticker);
        return cache.getIfPresent(ticker);
    }

    
    public void clearCache() {
        cache.invalidateAll();
    }
}