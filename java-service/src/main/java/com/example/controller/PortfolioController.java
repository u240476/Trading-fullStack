package com.example.controller;

import java.io.IOException;
import java.util.Calendar;

import org.ojalgo.optimisation.Optimisation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ExpectedReturnsPortfolioResponse;
import com.example.dto.ExpectedReturnsResponse;
import com.example.dto.GraphDataResponse;
import com.example.dto.GraphMVPDataResponse;
import com.example.dto.GraphTPResponse;
import com.example.dto.MVPResponse;
import com.example.dto.STDVPortfolioResponse;
import com.example.dto.StandardDeviationResponse;
import com.example.dto.TPResponse;
import com.example.dto.VariancePortfolioResponse;
import com.example.dto.VarianceResponse;
import com.example.dto.GraphPortfolioDataResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.InverseMatrixCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.PortfolioReturnCalculator;
import com.example.math.PortfolioStandardDeviationCalculator;
import com.example.math.PortfolioVarianceCalculator;
import com.example.math.StandardDeviationCalculator;
import com.example.math.UserPortfolioWeights;
import com.example.math.VarianceCalculator;
import com.example.optimisation.MVPWeights;
import com.example.optimisation.TPWeights;
import com.example.service.PortfolioService;



@RestController
public class PortfolioController {

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


    @GetMapping("/expected-portfolio-return")
    public ExpectedReturnsPortfolioResponse getExpectedReturnsPortfolio(
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
        
        double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, weights);

        return new ExpectedReturnsPortfolioResponse(portfolioReturn);
    }

    @GetMapping("/portfolio-variance")
    public VariancePortfolioResponse getExpectedVariancePortfolio(
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

        return new VariancePortfolioResponse(portfolioVariance);
    }
    @GetMapping("/portfolio-standard-deviation")
    public STDVPortfolioResponse getExpectedSTDVPortfolio(
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

        return new STDVPortfolioResponse(portfolioStandardDeviation);
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
        
        double[] tpWeights =
        //the 0.0025 is a placeholder for the average monthly return of a 3 year treasury bill it will be replaced by real data
                TPWeights.CalculatingTangencyPortfolio(inverseMatrix, expected, 0.0025);
                return new TPResponse(tickerArray, tpWeights);
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
        
        double[] tpWeights =
        //the 0.0025 is a placeholder for the average monthly return of a 3 year treasury bill it will be replaced by real data
                TPWeights.CalculatingTangencyPortfolio(inverseMatrix, expected, 0.0025);
                
        double portfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, tpWeights);

        double portfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(portfolioVariance);

        double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, tpWeights);
        return new GraphTPResponse(portfolioReturn, portfolioStandardDeviation);
    }
}
