package com.example.optimisation;

public class MinRiskForGivenReturn {
    public static double[] CalculatingForGivenRisk(
        double[][] inverse,
        double[] ExpReturns,
        double STDV
    ){
        int n = inverse.length;
        double A = 0.0, B = 0.0, C = 0.0;
        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                A += inverse[i][j];
                B += inverse[i][j] * ExpReturns[j];
                C += ExpReturns[i] * inverse[i][j] * ExpReturns[j];
            }
        }
        double D = A * C - Math.pow(B, 2.0);
        double var = Math.pow(STDV, 2.0);
        //possible cause of white page error
        /*if(var < 1/A){
            throw new IllegalArgumentException("portfolio cannot be on the efficient frontier given that the variance is less than the MVP");
        }*/
        double discriminant = Math.pow(B, 2.0) - A*(C-(D*var));
        if (discriminant < 0) {
            throw new IllegalArgumentException("No feasible portfolio exists for this risk level");
        }
        double R1 = (B + Math.sqrt(discriminant))/A;
        double R2 = (B - Math.sqrt(discriminant))/A;

        double R = Math.max(R1, R2);
        
        double leftformula = (C-B*R)/D;
        double rightformula = (A*R-B)/D;
        double[] weights = new double[n];
        for(int i = 0; i<n; i++){
            double wi = 0.0;
            for(int j = 0; j<n; j++){
                wi += inverse[i][j]*(leftformula + rightformula * ExpReturns[j]);
            }
            weights[i] = wi;
        }
        
        return weights;
    }
}
