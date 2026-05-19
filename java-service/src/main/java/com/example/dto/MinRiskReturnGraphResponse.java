package com.example.dto;

public class MinRiskReturnGraphResponse {
    private double minPortfolioStandardDeviation;
    private double minPortfolioReturn;

    public MinRiskReturnGraphResponse(double minPortfolioStandardDeviation, double minPortfolioReturn){
        this.minPortfolioStandardDeviation = minPortfolioStandardDeviation;
        this.minPortfolioReturn = minPortfolioReturn;
    }

    public double getMinPortfolioStandardDeviation(){
        return minPortfolioStandardDeviation;
    }

    public double getminPortfolioReturn(){
        return minPortfolioReturn;
    }
}
