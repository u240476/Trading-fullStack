package com.example.dto;

public class GraphDataResponse {
    private double expectedReturn;
    private double standardDeviation;
    public GraphDataResponse(double expectedReturn, double standardDeviation){
        this.expectedReturn = expectedReturn;
        this.standardDeviation = standardDeviation;
    }

    public double getExpectedReturn(){
        return expectedReturn;
    }
    public double getStandardDeviation(){
        return standardDeviation;
    }
}
