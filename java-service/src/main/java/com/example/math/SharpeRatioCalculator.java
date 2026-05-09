package com.example.math;

public class SharpeRatioCalculator {
    public static double CalculatingSharpeRatio(double STDV, double ExpReturn, double Rf){
        double sharpeRatio = ((ExpReturn-Rf)/ STDV);
        return sharpeRatio;
    }
}
