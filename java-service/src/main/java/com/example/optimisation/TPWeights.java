package com.example.optimisation;

public class TPWeights {

    public static double[] CalculatingTangencyPortfolio(
            double[][] inverse,
            double[] ExpReturns,
            double rf
    ) {

        int n = ExpReturns.length;

        // excess returns
        double[] excessReturns = new double[n];

        for (int i = 0; i < n; i++) {
            excessReturns[i] = ExpReturns[i] - rf;
        }

        // raw tangency vector
        double[] rawWeights = new double[n];

        for (int i = 0; i < n; i++) {

            double sum = 0.0;

            for (int j = 0; j < n; j++) {
                sum += inverse[i][j] * excessReturns[j];
            }

            rawWeights[i] = sum;
        }

        // normalize weights to sum to 1
        double total = 0.0;

        for (int i = 0; i < n; i++) {
            total += rawWeights[i];
        }

        double[] weights = new double[n];

        for (int i = 0; i < n; i++) {
            weights[i] = rawWeights[i] / total;
        }

        return weights;
    }
}