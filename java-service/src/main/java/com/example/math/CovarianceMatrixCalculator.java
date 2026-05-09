package com.example.math;

public class CovarianceMatrixCalculator {
    public static double[][] varianceCovarianceMatrix( double[][] returns, double[] means)
    {
        int numStocks = returns[0].length;
        double[][] covMatrix = new double[numStocks][numStocks];


        for (int i = 0; i < numStocks; i++) {
            for (int j = i; j < numStocks; j++) {
                double sum = 0.0;
                for (double[] t: returns) { 
                    sum += (t[i] - means[i]) * (t[j] - means[j]);
                }
                covMatrix[i][j] = sum / (returns.length - 1);
                covMatrix[j][i] = covMatrix[i][j]; 
            }
        }

        return covMatrix;

    }
}
