package com.example.math;

public class UserPortfolioWeights{
    public static double[] CalculatingUserWeights(double[] proportions){
        int size = proportions.length;
        double[] weights = new double[size];
        double proportionsSum = 0.0;
        for(int i = 0; i < size; i++){
            proportionsSum += proportions[i];
        }
        for(int i = 0; i < size; i++){
            weights[i] = (proportions[i]/proportionsSum);
        }
        return weights;
    }
}