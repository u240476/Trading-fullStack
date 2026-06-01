package com.example.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.example.dto.RfAssetResponse;

@Service
public class RfRateService {
    private final PortfolioService portfolioService;

    public RfRateService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    public double[][] getPrices(String[] tickerArray, String interval){
        try{
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -10);

        double[][] prices =
                portfolioService.getPriceMatrix(
                        tickerArray,
                        start,
                        end,
                        interval
                );
        return prices;
        }catch (IOException e) {
                throw new RuntimeException(e);
        }
    }

    public RfAssetResponse getRf(){

        String[] tickerArray = { "^IRX" };

        double[][] prices = getPrices(tickerArray, "1mo");

        double avgYield = Arrays.stream(prices[0])
                        .filter(d -> !Double.isNaN(d))
                        .average()
                        .orElseThrow();
        
        double rfAssetYield = avgYield/100.0;
        
        return new RfAssetResponse(rfAssetYield);
    }
}
