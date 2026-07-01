package com.example.math;

public class BetaCalculator {
    public static double CalculateBeta(double[][] covMatrix , double marketVariance){
        
        if(covMatrix.length != 2 || covMatrix[0].length != 2){
            throw new IllegalArgumentException("expected a 2 x 2 covariance matrix");
        }
        
        double covar = covMatrix[0][1];
        return covar/marketVariance;
    }
}
