package com.example.math;

public class StandardDeviationCalculator {
    
    public static double[] CalculatingStockSTDV(double[] variance){
        double[] Sqrt = new double[variance.length];
        for(int i = 0; i < Sqrt.length; i++) {
            Sqrt[i] = Math.sqrt(variance[i]);
        }
        return Sqrt;
    }
}
