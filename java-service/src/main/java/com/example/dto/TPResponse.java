package com.example.dto;

public class TPResponse {
    private String[] tickers;
    private double[] weights;

    public TPResponse(String[] tickers, double[] weights) {
        this.tickers = tickers;
        this.weights = weights;
    }

    public String[] getTickers() {
        return tickers;
    }

    public double[] getWeights() {
        return weights;
    }
}
