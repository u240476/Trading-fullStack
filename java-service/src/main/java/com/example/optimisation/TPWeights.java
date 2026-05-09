package com.example.optimisation;

//NOTE NOT WORKING NEEDS TO BE FULLY TESTED
//TENDENCY TO DEVIATE TO FULL WEIGHT IN ONE STOCK AND HUGE NEGATIVE WEIGHTS IN ANOTHER 
public class TPWeights {
    public static double[] CalculatingTangencyPortfolio(
         double[][] inverse,
         double[] ExpReturns,
         double rf
    ){
        double[] vector = new double[inverse.length];
        double[] riskPremium = new double[ExpReturns.length];
        for(int i = 0; i < riskPremium.length; i++){
            riskPremium[i] = ExpReturns[i] - rf;
        }
        
        double denom = 0.0;
        for(int i = 0; i < inverse.length; i++){
            double sum = 0.0;
            for(int j = 0; j < inverse.length; j++){
                sum += inverse[i][j]* riskPremium[j];
               
            }
            vector[i] = sum;
            denom += vector[i];
        }
        double[] weights = new double[inverse.length];
        for(int i = 0; i<weights.length; i++){
            weights[i] = vector[i]/denom;
        }
        return weights;
    }
}
