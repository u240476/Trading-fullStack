package com.example.controller;

import java.io.IOException;
import java.util.Calendar;

import org.ojalgo.optimisation.Optimisation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.GraphMVPDataResponse;
import com.example.dto.GraphTPResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.InverseMatrixCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.PortfolioReturnCalculator;
import com.example.math.PortfolioStandardDeviationCalculator;
import com.example.math.PortfolioVarianceCalculator;
import com.example.optimisation.MVPWeights;
import com.example.optimisation.TPWeights;
import com.example.service.PortfolioService;
import com.example.service.RfRateService;

@RestController
public class GraphOptimisationController {

    private final PortfolioService portfolioService;
    private final RfRateService rfRateService;

    public GraphOptimisationController(
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
    
     @GetMapping("/graph-mvp-data")
    public GraphMVPDataResponse getGraphMVPData(
        @RequestParam String tickers,
        @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {
        try{
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

        double portfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, mvpWeights);

        double portfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(portfolioVariance);

        double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, mvpWeights);


        
        return new GraphMVPDataResponse(portfolioReturn, portfolioStandardDeviation);
        } catch (Exception e) {
        e.printStackTrace();  
        throw e;
        }
    }
    @GetMapping("/graph-tp-data")
    public GraphTPResponse getGraphTPData(
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
                
        double portfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, tpWeights);

        double portfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(portfolioVariance);

        double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, tpWeights);
        return new GraphTPResponse(portfolioReturn, portfolioStandardDeviation);
    }

}
