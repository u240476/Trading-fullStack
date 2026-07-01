package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.BetaResponse;
import com.example.dto.ExpectedReturnsResponse;
import com.example.dto.StandardDeviationResponse;
import com.example.dto.VarianceResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.StandardDeviationCalculator;
import com.example.math.VarianceCalculator;
import com.example.service.PortfolioService;

@RestController
public class StockController {
    private final PortfolioService portfolioService;

   public StockController(PortfolioService portfolioService) {
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

    @GetMapping("/expected-return")
    public ExpectedReturnsResponse getExpectedReturns(
            @RequestParam String tickers,
            @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {

        String[] tickerArray = tickers.split(",");

        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        return new ExpectedReturnsResponse(tickerArray, expected);
    }

    @GetMapping("/variance")
    public VarianceResponse getVariance(
            @RequestParam String tickers,
            @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {

        String[] tickerArray = tickers.split(",");

        double[][] prices = getPrices(tickerArray, interval);   

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[] variance = 
                VarianceCalculator.CalculatingVariance(returns, expected);
        

        return new VarianceResponse(tickerArray, variance);
    }

    @GetMapping("/standard-deviation")
    public StandardDeviationResponse getStandardDeviation(
        @RequestParam String tickers,
        @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {

        String[] tickerArray = tickers.split(",");
        
        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[] variance = 
                VarianceCalculator.CalculatingVariance(returns, expected);
        
        double[] standardDeviation = 
                StandardDeviationCalculator.CalculatingStockSTDV(variance);
                
        

        return new StandardDeviationResponse(tickerArray, standardDeviation);
    }
    
    @GetMapping("/beta-stock")
        public BetaResponse getBeta(
                @RequestParam String tickers,
                @RequestParam(defaultValue = "1mo") String interval
        ) throws IOException {

        String[] tickerArray = tickers.split(",");

        String[] fullTickers = Arrays.copyOf(tickerArray, tickerArray.length + 1);
        fullTickers[tickerArray.length] = "^GSPC";

        double[][] prices = getPrices(fullTickers, interval);

        double[][] returns =
            LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
            ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[][] covMatrix =
            CovarianceMatrixCalculator.varianceCovarianceMatrix(returns, expected);

        double[] variance =
            VarianceCalculator.CalculatingVariance(returns, expected);

        int marketIndex = -1;
        for (int i = 0; i < fullTickers.length; i++) {
                if (fullTickers[i].equals("^GSPC")) {
                marketIndex = i;
                break;
                }
        }

        if (marketIndex == -1) {
                throw new RuntimeException("Market index not found");
        }
        double marketVariance = variance[marketIndex];

        List<Double> betas = new ArrayList<>();

        for (int i = 0; i < tickerArray.length; i++) {

                double beta =
                covMatrix[i][marketIndex] / marketVariance;

                betas.add(beta);
        }

        return new BetaResponse(
            Arrays.asList(tickerArray),
            betas
        );
        }
}
