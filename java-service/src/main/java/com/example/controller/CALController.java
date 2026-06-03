package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CalPoint;
import com.example.math.CovarianceMatrixCalculator;
import com.example.math.ExpMonthlyReturnsCalculator;
import com.example.math.InverseMatrixCalculator;
import com.example.math.LogarithmicReturnsCalculator;
import com.example.math.PortfolioReturnCalculator;
import com.example.math.PortfolioStandardDeviationCalculator;
import com.example.math.PortfolioVarianceCalculator;
import com.example.math.SharpeRatioCalculator;
import com.example.optimisation.TPWeights;
import com.example.service.PortfolioService;
import com.example.service.RfRateService;

@RestController
public class CALController {
    private final PortfolioService portfolioService;
    private final RfRateService rfRateService;

    public CALController(
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

    @GetMapping("/capital-allocation-line")
    public List<CalPoint> getCAL(
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
        
        double sharpeRatio = 
                SharpeRatioCalculator.CalculatingSharpeRatio(portfolioStandardDeviation, portfolioReturn, rf);
        
        List<CalPoint> calPoints = new ArrayList<>();

        for(double risk = 0.0; risk <= portfolioStandardDeviation*2; risk+=portfolioStandardDeviation/25){
            calPoints.add(
                new CalPoint(

                risk,
                rf + sharpeRatio * risk

                )
            );
        }

        return calPoints;
    }
}
