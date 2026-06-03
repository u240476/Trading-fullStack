package com.example.dto;

public class CalPoint {
    private double risk;
    private double expectedReturn;

    public CalPoint(double risk, double expectedReturn){
        this.risk = risk;
        this.expectedReturn = expectedReturn;
    }

    public double getRisk() {
        return risk;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }
}
