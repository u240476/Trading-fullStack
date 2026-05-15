package com.example.dto;

public class GraphPortfolioDataResponse {
    private double portfolioReturn;
    private double portfolioStandardDeviation;

    public GraphPortfolioDataResponse(double portfolioReturn, double portfolioStandardDeviation){
        this.portfolioReturn = portfolioReturn;
        this.portfolioStandardDeviation = portfolioStandardDeviation;
    }

    public double getPortfolioReturn(){
        return portfolioReturn;
    }
    public double getPortfolioStandardDeviation(){
        return portfolioStandardDeviation;
    }
}
