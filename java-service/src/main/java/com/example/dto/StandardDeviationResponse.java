package com.example.dto;

public class StandardDeviationResponse {
    private String[] tickers;
    private double[] standardDeviation;

    public StandardDeviationResponse(String[] tickers, double[] standardDeviation) {
        this.tickers = tickers;
        this.standardDeviation = standardDeviation;
    }

    public String[] tickers() {
        return tickers;
    }

    public double[] standardDeviation() {
        return standardDeviation;
    }
    
}
