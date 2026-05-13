package com.example.dto;

public class VarianceResponse {
    private String[] tickers;
    private double[] variance;

    public VarianceResponse(String[] tickers, double[] variance) {
        this.tickers = tickers;
        this.variance = variance;
    }

    public String[] getTickers() {
        return tickers;
    }

    public double[] getVariance() {
        return variance;
    }
}
