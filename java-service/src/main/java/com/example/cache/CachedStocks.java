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
                    .expireAfterWrite(Duration.ofHours(24))
                    .build();

    
    public void createEntry(String ticker, double[] prices) {
        if(prices.length >= 120){
        Stock stock = new Stock.Builder(prices)
                .build();

        cache.put(ticker.toUpperCase(), stock);
        }
    }

    
    public Stock getEntry(String input) {
        String ticker = input.toUpperCase();
        Stock stock = cache.getIfPresent(ticker);

        if (stock != null) {
            System.out.println("CACHE HIT: " + ticker);
        }
        return stock;
    }

    
    public void clearCache() {
        cache.invalidateAll();
    }
}