package com.example.math;

public class PortfolioReturnCalculator {
    public static double CalculatingPortfolioReturn(double[] EmonthlyReturns, double[] Weights){
        int size = EmonthlyReturns.length;
        double ExpectedReturn = 0.0;
        for(int i = 0; i<size; i++){
            ExpectedReturn += (EmonthlyReturns[i]*Weights[i]);
        }
        return ExpectedReturn;
    }
}
