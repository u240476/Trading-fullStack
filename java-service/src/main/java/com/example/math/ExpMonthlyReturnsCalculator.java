package com.example.math;

public class ExpMonthlyReturnsCalculator {
    public static double[] ExpectedMonthlyReturns(double returnData[][]){
        double[] ExpectedMonthlyReturns = new double[returnData[0].length];
        double sum = 0.0;
        for(int collumn = 0; collumn < returnData[0].length; collumn++){
           for(double[] row: returnData ){
             sum+= row[collumn];
           }
           ExpectedMonthlyReturns[collumn] = (sum/returnData.length);
           sum = 0.0;
        }
        return ExpectedMonthlyReturns;
    }
}
