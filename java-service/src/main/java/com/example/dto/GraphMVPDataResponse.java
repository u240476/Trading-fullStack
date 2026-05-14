package com.example.dto;

public class GraphMVPDataResponse {
    private double portfolioReturn;
    private double portfolioStandardDeviation;

    public GraphMVPDataResponse(double portfolioReturn, double portfolioStandardDeviation){
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
