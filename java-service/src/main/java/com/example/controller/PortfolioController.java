package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.ExpectedReturnsPortfolioResponse;
import com.example.dto.MinRiskReturnGraphResponse;
import com.example.dto.PricePoint;
import com.example.dto.PriceReturnResponse;
import com.example.dto.STDVPortfolioResponse;
import com.example.dto.VariancePortfolioResponse;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.InverseMatrixCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.PortfolioReturnCalculator;
import com.example.math.PortfolioStandardDeviationCalculator;
import com.example.math.PortfolioVarianceCalculator;
import com.example.math.UserPortfolioWeights;
import com.example.optimisation.MinRiskForGivenReturn;
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
        

    /*@GetMapping("/expected-return")
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
    }*/


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
    
    /*
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
        */
    /* 
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
    }*/
   /* 
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
    }*/

    
    @GetMapping("/graph-price-return")
    public PriceReturnResponse getPriceReturn(
            @RequestParam String ticker,
            @RequestParam(defaultValue = "1mo") String interval
    ) throws IOException {

        String[] tickerArray = { ticker };

        double[][] prices = getPrices(tickerArray, interval);


        ArrayList<PricePoint> points = new ArrayList<>();

        int expectedColumns = 1;

        for (double[] price : prices) {
                if (price.length != expectedColumns) {
                    throw new IllegalStateException(
                            "Expected 1 column per row but found " + price.length
                    );
                }
        }
        for(int i = 0; i<prices.length; i++){
                points.add( new PricePoint(prices[i][0], i));
        }

        return new PriceReturnResponse(points);
    }
    @GetMapping("/graph-min-risk-for-return")
        public MinRiskReturnGraphResponse getMinRiskForGivenReturn(
            @RequestParam String tickers,
            @RequestParam String proportions,
            @RequestParam(defaultValue = "1mo") String interval
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
        
        double[][] inverseMatrix =
                InverseMatrixCalculator.pseudoInverse(covMatrix);
        
        double portfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, weights);
        

        double portfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(portfolioVariance);
        
        double portfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, weights);


        
        double[] minWeights = 
                MinRiskForGivenReturn.CalculatingForGivenRisk(inverseMatrix, expected, portfolioStandardDeviation);
        
        double minPortfolioVariance =
                PortfolioVarianceCalculator.CalculatingPortfolioVariance(covMatrix, minWeights);

        double minPortfolioStandardDeviation = 
                PortfolioStandardDeviationCalculator.CalculatingPortfolioSTDV(minPortfolioVariance);
        
        double minPortfolioReturn =
                PortfolioReturnCalculator.CalculatingPortfolioReturn(expected, minWeights);
        
        return new MinRiskReturnGraphResponse(minPortfolioStandardDeviation, minPortfolioReturn);

    }
}
