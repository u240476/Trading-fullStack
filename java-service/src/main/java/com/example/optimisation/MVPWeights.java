package com.example.optimisation;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class MVPWeights {
    public static Optimisation.Result CalculatingMVPWeights(double [][] covariance){
       int n = covariance.length;

    ExpressionsBasedModel model = new ExpressionsBasedModel();

    // Weight variables: constrain total negative weights to -0.2
    Variable[] w = new Variable[n];
    for (int i = 0; i < n; i++) {
        w[i] = Variable.make("w" + i).lower(-0.2);
        model.addVariable(w[i]);
    }

    // Budget constraint: weights must equal 1
    var budget = model.addExpression("budget").level(1.0);
    for (int i = 0; i < n; i++) {
        budget.set(w[i], 1.0);
    }

    // Objective: minimise portfolio variance 
    var variance = model.addExpression("variance").weight(1.0);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            variance.set(w[i], w[j], covariance[i][j]);
        }
    }
    return model.minimise();
    }
}
