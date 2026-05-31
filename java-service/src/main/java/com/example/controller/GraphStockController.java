package com.example.controller;

import java.io.IOException;
import java.util.Calendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.GraphDataResponse;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.StandardDeviationCalculator;
import com.example.math.VarianceCalculator;
import com.example.service.PortfolioService;

@RestController
public class GraphStockController {
    private final PortfolioService portfolioService;

    public GraphStockController(PortfolioService portfolioService) {
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
    
    @GetMapping("/graph-data")
    public GraphDataResponse getGraphData(
        @RequestParam String ticker,
        @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {
        String tickerArray[] = { ticker };
        
        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[] variance = 
                VarianceCalculator.CalculatingVariance(returns, expected);
        
        double[] standardDeviation = 
                StandardDeviationCalculator.CalculatingStockSTDV(variance);

        //only one stock submitted at a time so if the express checks fail and more than one stock gets to this point 
        //only return the data related to the first stock entered, LAST SANITY CHECK!
        return new GraphDataResponse(expected[0], standardDeviation[0]);
    }
}
