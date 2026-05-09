package com.example.math;

public class VarianceCalculator{
    public static double[] CalculatingVariance(double returns[][], double averageReturns[]){
        //diff to find (R-average R) and sumSquaredDiff to find (R-average R)^2 and keep already calculated returns stored
        double VarianceTerms[] = new double[returns[0].length];
        double diff;
        
        for(int col = 0; col < returns[0].length; col++) {
            double sumSquaredDiff = 0.0;
            for(double[] i: returns){
                diff = i[col] - averageReturns[col];
                sumSquaredDiff += diff * diff;
                
            }
            VarianceTerms[col] = sumSquaredDiff / (returns.length - 1);
        }
        return VarianceTerms;
    }
}
