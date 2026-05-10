package com.example.dto;

public class VarianceResponse {
    private String[] tickers;
    private double[] variance;

    public VarianceResponse(String[] tickers, double[] variance) {
        this.tickers = tickers;
        this.variance = variance;
    }

    public String[] tickers() {
        return tickers;
    }

    public double[] variance() {
        return variance;
    }
}
