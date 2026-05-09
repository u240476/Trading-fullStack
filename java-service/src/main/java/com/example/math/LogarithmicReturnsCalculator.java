package com.example.math;

public class LogarithmicReturnsCalculator {
        public static double[][] CalculatingReturnMatrix(double[][] rawData){
        //creating a new array to transfer newly calculated data with n-1 terms as we drop the first term in rawData
        double[][] returns = new double[rawData.length-1][rawData[0].length];
        double ratio;
        for(int collumn = 0; collumn<rawData[0].length; collumn++) { 
            //only go up to rawdata.length-1 as we are calculating using the ith and i+1th term to avoid index out of bounds
            for(int i = 0; i < rawData.length-1; i++){
                ratio = rawData[i+1][collumn]/ rawData[i][collumn];
                //Math.log cannot deal with 0 or negative numbers, cast numbers as positive then recast as negative
                if(ratio>0){
                    returns[i][collumn] = Math.log(ratio);
                }
                else if(ratio < 0){
                    ratio *= -1;
                    returns[i][collumn] = Math.log(ratio) * -1;
                }
                else{
                    returns[i][collumn] = 0;
                }
            }
        }
        return returns;
    }
}
