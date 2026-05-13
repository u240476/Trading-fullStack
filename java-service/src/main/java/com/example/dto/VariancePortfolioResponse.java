package com.example.dto;

public class VariancePortfolioResponse {
    private double portfolioVariance;

    public VariancePortfolioResponse(double portfolioVariance) {
        this.portfolioVariance = portfolioVariance;
    }

    public double getPortfolioVariance() {
        return portfolioVariance;
    }
}
