package com.example.math;

public class PortfolioVarianceCalculator {
    public static double CalculatingPortfolioVariance(double[][] coVar, double[] weights) {
    double totalVariance = 0.0;

    for (int i = 0; i < coVar.length; i++) {
        totalVariance += weights[i] * weights[i] * coVar[i][i];
    }

    for (int i = 0; i < coVar.length; i++) {
        for (int j = i + 1; j < coVar.length; j++) {
            totalVariance += 2 * weights[i] * weights[j] * coVar[i][j];
        }
    }
    return totalVariance;
    }
}
