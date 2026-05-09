package com.example.optimisation;

public class MaxReturnsForGivenReturn {
    public static double[] CalculatingForGivenReturn(
        double [][] inverse,  
        double[] ExpReturns,
        double R
     ){
        int n = inverse.length;
        double A = 0.0, B = 0.0 , C = 0.0 ;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                A += inverse[i][j];
                B += inverse[i][j] * ExpReturns[j];
                C += ExpReturns[i] * inverse[i][j] * ExpReturns[j];
            }
        }
        double D = A * C - Math.pow(B, 2.0);
        double leftformula = (C-B*R)/D;
        double rightformula = (A*R-B)/D;
        double[] weights = new double[n];
        for(int i = 0; i<n; i++){
            double wi = 0.0;
            for(int j = 0; j<n; j++){
                wi += inverse[i][j]*(leftformula + rightformula * ExpReturns[j]);
            }
            weights[i] = wi;
        }
        //this method cannot be compared to ojalgo methods with constraints on weights.
        //can only be used against user portfolio
        return weights;
    }
}
