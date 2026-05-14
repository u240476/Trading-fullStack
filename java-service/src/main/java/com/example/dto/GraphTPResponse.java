package com.example.dto;

public class GraphTPResponse {

    private double portfolioReturn;
    private double portfolioStandardDeviation;

    public GraphTPResponse(double portfolioReturn, double portfolioStandardDeviation) {
        this.portfolioReturn = portfolioReturn;
        this.portfolioStandardDeviation = portfolioStandardDeviation;
    }

    public double getPortfolioReturn() {
        return portfolioReturn;
    }

    public double getPortfolioStandardDeviation() {
        return portfolioStandardDeviation;
    }
}