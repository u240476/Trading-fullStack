package com.example.controller;

import java.io.IOException;
import java.util.Calendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.GraphPortfolioDataResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.PortfolioReturnCalculator;
import com.example.math.PortfolioStandardDeviationCalculator;
import com.example.math.PortfolioVarianceCalculator;
import com.example.math.UserPortfolioWeights;
import com.example.service.PortfolioService;

@RestController
public class GraphPortfolioController {

    public double[][] getPrices(String[] tickerArray, String interval){
        try{
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -10);

        double[][] prices =
                PortfolioService.getPriceMatrix(
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
    
    @GetMapping("/graph-portfolio-data")
    public GraphPortfolioDataResponse getGraphPortfolioData(
            @RequestParam String tickers,
            @RequestParam(defaultValue = "1mo") String interval,
            @RequestParam String proportions
    ) throws IOException {

        String[] tickerArray = tickers.split(",");
        String[] proportionsStringArray = proportions.split(",");

        int l = proportionsStringArray.length;

        double[] proportionsArray = new double[l];
        for(int i = 0; i<l; i++){
                proportionsArray[i] = Double.parseDouble(proportionsStringArray[i]);
        }

        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[] weights =
                UserPortfolioWeights.CalculatingUserWeights(proportionsArray);
        
        double[][] covMatrix  = 
                CovarianceMatrixCalculator.varianceCovarianceMatrix(returns, expected);
        
        double portfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, weights);

        double portfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(portfolioVariance);
        
                double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, weights);


        return new GraphPortfolioDataResponse(portfolioReturn, portfolioStandardDeviation);
    }
}
