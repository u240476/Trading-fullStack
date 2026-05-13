package com.example.dto;

public class STDVPortfolioResponse {
    private double portfolioStandardDeviation;

    public STDVPortfolioResponse(double portfolioStandardDeviation) {
        this.portfolioStandardDeviation = portfolioStandardDeviation;
    }

    public double getPortfolioStandardDeviation() {
        return portfolioStandardDeviation;
    }

}
