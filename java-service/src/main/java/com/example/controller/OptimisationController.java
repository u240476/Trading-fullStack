package com.example.controller;


import java.io.IOException;
import java.util.Calendar;

import org.ojalgo.optimisation.Optimisation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.MVPResponse;
import com.example.dto.TPResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.InverseMatrixCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.optimisation.MVPWeights;
import com.example.optimisation.TPWeights;
import com.example.service.PortfolioService;
import com.example.service.RfRateService;

@RestController
public class OptimisationController {
    private final PortfolioService portfolioService;
    private final RfRateService rfRateService;

    public OptimisationController(
        PortfolioService portfolioService, 
        RfRateService rfRateService
    ){
        this.portfolioService = portfolioService;
        this.rfRateService = rfRateService;
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

    @GetMapping("/mvp")
    public MVPResponse getMVP(
        @RequestParam String tickers,
        @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {
        String[] tickerArray = tickers.split(",");

        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[][] covMatrix  = 
                CovarianceMatrixCalculator.varianceCovarianceMatrix(returns, expected);

        Optimisation.Result result = 
                MVPWeights.CalculatingMVPWeights(covMatrix);
        
        double[] mvpWeights = new double[covMatrix.length];
        for (int i = 0; i < mvpWeights.length; i++) {
                mvpWeights[i] = result.get(i).doubleValue();
        }  
                
        
        return new MVPResponse(tickerArray, mvpWeights);
    }
    @GetMapping("/tp")
    public TPResponse getTP(
        @RequestParam String tickers,
        @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {
        String[] tickerArray = tickers.split(",");

        double[][] prices = getPrices(tickerArray, interval);

        double[][] returns =
                LogarithmicReturnsCalculator.CalculatingReturnMatrix(prices);

        double[] expected =
                ExpMonthlyReturnsCalculator.ExpectedMonthlyReturns(returns);

        double[][] covMatrix  = 
                CovarianceMatrixCalculator.varianceCovarianceMatrix(returns, expected);

        double[][] inverseMatrix =
                InverseMatrixCalculator.pseudoInverse(covMatrix);
        
        double rf = rfRateService.getRf().getRfAssetYield();

        double[] tpWeights =
                TPWeights.CalculatingTangencyPortfolio(inverseMatrix, expected, rf);
                return new TPResponse(tickerArray, tpWeights);
    }
}
