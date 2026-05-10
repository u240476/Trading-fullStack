package com.example.dto;

public class ExpectedReturnsResponse {

    private String[] tickers;
    private double[] expectedReturns;

    public ExpectedReturnsResponse(String[] tickers, double[] expectedReturns) {
        this.tickers = tickers;
        this.expectedReturns = expectedReturns;
    }

    public String[] getTickers() {
        return tickers;
    }

    public double[] getExpectedReturns() {
        return expectedReturns;
    }
}