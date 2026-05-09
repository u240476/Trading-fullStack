package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/** 
* This Class provides all of the calculations for my portfolio selection and analysis project
* it includes 21 mmethods for making various financial calculations
* @author Joshue Reynolds
* @version 1.0
* @since 01/01/2026
*/
public class TradingBotv2 
{
    /** 
    * main method asks the user to input how many securities they would like to invest in and their ticker values
    * makes calls to the 20 other methods in the project to allow them to optimise their investing
    * Asks user to input the interval they would like to use
    * also asks the user for the percentage they want to invest in the risk free asset and a level of standard deviation
    that they would like to invest with
    * @param args
    */
	public static void main(String[] args) 
	{
        Scanner scan2 = new Scanner(System.in);
        
        System.out.println("how many stocks would you like to invest in?");
        int numOfStocks = Integer.parseInt(scan2.nextLine());
        String[] tickers = new String[numOfStocks];
       
        System.out.println("please input the ticker of the stock you would like to invest in");
        for(int i = 0; i < numOfStocks; i++){
            tickers[i] = (scan2.nextLine()).toUpperCase();
        }
        
        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -10);
        end.add(Calendar.YEAR, 0);
        System.out.println("please enter the interval you would like, daily, weekly or monthly");
        System.out.println("note choose a larger interval for long term investing and visa-versa");
        String userInterval = scan2.nextLine();
        String interval;
        
        switch(userInterval.toLowerCase()){
            case "daily" ->
                interval = "1d";
            case "weekly" ->
                interval = "1wk";
            case "monthly" ->
                interval = "1mo";
            default ->
                throw new IllegalArgumentException("intervals must be daily weekly or monthly");
        }
        double [][] input = null;
        try {
            input = getPriceMatrix(
                tickers,
                start,
                end,
                interval
            );
         } catch(IOException e){
            System.out.println("IO exception throw during api");
            return;
         }
         double intervalToYear = 0.0;
         switch(userInterval.toLowerCase()){
            case "daily" ->
                intervalToYear = 364.0;
            case "weekly" ->
                intervalToYear = 52.0;
            case "monthly" ->
                intervalToYear = 12.0; 
         }
          //risk free asset to be found using api call later
        double riskFreeAsset = 0.02/intervalToYear;
        double returns[][] = CalculatingReturnMatrix(input);
        double[] EmonthlyReturns = ExpectedMonthlyReturns(returns);
        double[] Variance = CalculatingVariance(returns, EmonthlyReturns);
        double[] standardDev = CalculatingStockSTDV(Variance);
        double[][] CoVar = varianceCovarianceMatrix(returns, EmonthlyReturns);
        double[][] coVarInverse = pseudoInverse(CoVar);
        double[] Covariance = CovarianceTerms(CoVar);
        Optimisation.Result result = CalculatingMVPWeights(CoVar);

        for(int i = 0; i < tickers.length; i++){
            double varYear = Variance[i] * intervalToYear;
            double stdYear = Math.sqrt(varYear);
            System.out.printf("For stock %s yearly expected return: %.2f%% yearly stdev: %.2f%%, yearly variance: %.6f\n",
                tickers[i],
                EmonthlyReturns[i]*intervalToYear*100.0,
                stdYear*100.0,
                varYear*100.0
            );
            if(numOfStocks == 1){ 
                System.out.println("please input more than one stock if youd like to build a portfolio");
                System.exit(0);
            }


        }

    double[] MVPWeights = new double[CoVar.length];
    for (int i = 0; i < MVPWeights.length; i++) {
        MVPWeights[i] = result.get(i).doubleValue();  
        System.out.printf("The weight of %s in the MVP is: %.2f%%%n",
        tickers[i],
        MVPWeights[i]*100.0
        );     
    }

        double ExpectedReturnOfMVP = CalculatingPortfolioReturn(EmonthlyReturns, MVPWeights);
        double MVPVariance = CalculatingPortfolioVariance(CoVar, MVPWeights)*intervalToYear;
        double MVPStandardDeviation = CalculatingPortfolioSTDV(MVPVariance);
        // printing standard deviation of MVP
        System.out.printf("Yearly Expected returns of the MVP is: %.2f%%%n", ExpectedReturnOfMVP*intervalToYear*100.0);
        System.out.printf("The yearly Standard deviation of your Minimum Variance portfolio is %.2f%%%n", MVPStandardDeviation*100.0);

        // here i will give the user an option to input how much money they were planning to 
        // invest in each stock of their portfolio. then i will calculate these results again
        System.out.println("please input the ammount of money that you would like to invest in each stock");
        double[] userProportions = new double[numOfStocks];
        for(int i = 0; i < numOfStocks; i++){
            userProportions[i] = Double.parseDouble(scan2.nextLine());
        }   
        double[] userWeights = CalculatingUserWeights(userProportions);
        double userExpectedReturn = CalculatingPortfolioReturn(EmonthlyReturns, userWeights);
        double userVariance = CalculatingPortfolioVariance(CoVar, userWeights)*intervalToYear;
        double[] weightsGivenReturn = CalculatingForGivenReturn(coVarInverse, EmonthlyReturns, userExpectedReturn);
        double efficientExpectedReturn = CalculatingPortfolioReturn(EmonthlyReturns, weightsGivenReturn);
        double efficientVariance = CalculatingPortfolioVariance(CoVar, weightsGivenReturn)*intervalToYear;
        double userSTDV = CalculatingPortfolioSTDV(userVariance);
        double efficientSTDV = CalculatingPortfolioSTDV(efficientVariance);
        if(userVariance > efficientVariance){
            System.out.println("Your portfolio is not efficient");
            System.out.println("there exists other combinations of weights which can increase your expected return without raising your risk");
            System.out.printf("Your Portfolios yearly standard deviation for an expected return of: %.2f%% is: %.2f%%%n",
                userExpectedReturn*100.0*intervalToYear,
                userSTDV*100.0
            );
            System.out.printf("The Efficient Portfolios yearly standard deviation for an expected return of: %.2f%% is: %.2f%%%n",
                efficientExpectedReturn*100.0*intervalToYear,
                efficientSTDV*100.0
            );
        }
       double[] TPWeights = CalculatingTangencyPortfolio(coVarInverse, EmonthlyReturns, riskFreeAsset);
        for(int i = 0; i < TPWeights.length; i++){
            System.out.printf("The weight of stock %s in the TP is %.2f%%%n",
            tickers[i],
            TPWeights[i]
        );
        }
        
        double TPExpectedReturn = CalculatingPortfolioReturn(EmonthlyReturns, TPWeights);
        double TPVariance = CalculatingPortfolioVariance(CoVar, TPWeights)*intervalToYear;
        double TPStandardDeviation = CalculatingPortfolioSTDV(TPVariance);
        double SharpeRatioTP = CalculatingSharpeRatio(TPStandardDeviation, TPExpectedReturn, riskFreeAsset);
        // printing standard deviation of TP
        System.out.printf("Yearly Expected returns of the Tangency Portfolio: %.2f%%%n", TPExpectedReturn*intervalToYear*100.0);
        System.out.printf("The yearly Standard deviation of your Tangency portfolio is %.2f%%%n", TPStandardDeviation*100.0);
        System.out.println("would you like me to caluculate your expected return on the CAL based on your weight of the rf asset and the tp? y/n");
        if(scan2.nextLine().equals("y")){
            System.out.println("give me a number between 0-100 that is the percent you would like to invest in the rf asster versus the tp");
            int percentageInRf = Integer.parseInt(scan2.nextLine());
            if(percentageInRf < 0 || percentageInRf > 100){
                System.out.println("invald number entered");
            }
            int percentageInTP = 100 - percentageInRf;
            double weightInRf = (percentageInRf/100.0);
            double weightInTP = (percentageInTP/100.0);
            System.out.println(weightInTP);
            System.out.println(TPExpectedReturn);
            double stdvOfCALPortfolio = CalculatingSTDVonCAL(TPStandardDeviation, weightInTP);
            double EreturnOfCALPortfolio = CalculatingExpReturnonCAL(TPExpectedReturn, weightInTP);
            //note that this does not yet take into account the returns on the risk free asset and needs to be modified
            System.out.printf(
                "your portfolio on the CAL has an expected return of %.2f and a Standard deviation of risk of %.2f%n",
                EreturnOfCALPortfolio*intervalToYear,
                stdvOfCALPortfolio*intervalToYear
            );
            System.out.println("enter a given standard deviation of risk so we can calculate the optimal portfolio");
            double givenSTDVRisk = Double.parseDouble(scan2.nextLine());
            double[] WeightsGivenSTDV = CalculatingForGivenRisk(coVarInverse, EmonthlyReturns, givenSTDVRisk);
            for(int i = 0; i < CoVar.length; i++)    {   
                System.out.printf("Optimal weight for stock %s given a STDV of %f is %.2f%%%n",
                    tickers[i],
                    givenSTDVRisk,
                    WeightsGivenSTDV[i]
                );
            }
            }
        scan2.close();


    }
    /**
     * Sets up an API call to Yahoo Finance to get 10 years of historic data for the securities chosen by the user
     * @param tickers
     * @param start
     * @param end
     * @param yahooInterval
     * @return  the historical price data for the requested tickers and date range
     * @throws IOException if the request fails
     */
    public static double[][] getPriceMatrix(
        String[] tickers,
        Calendar start,
        Calendar end,
        String yahooInterval
    ) throws IOException {

    long startUnix = start.getTimeInMillis() / 1000;
    long endUnix   = end.getTimeInMillis()   / 1000;

    double[][] prices = null;
    

    for (int i = 0; i < tickers.length; i++) {

        double[] series =
                downloadYahooAdjClose(
                        tickers[i],
                        startUnix,
                        endUnix,
                        yahooInterval
                );

        if (series.length == 0){
            throw new IllegalStateException("No data for " + tickers[i]);
        }

        if (prices == null){
            prices = new double[series.length][tickers.length];
        }

        for (int t = 0; t < series.length; t++){
            prices[t][i] = series[t];
        }
    }

    return prices;
}
/**
 * Helper method for GetPriceMatrix that performs the API call, downloads the returns and returns them to GetPriceMatrix
 * @param ticker
 * @param startUnix
 * @param endUnix
 * @param interval
 * @return the historical price data for the requested tickers and date range
 * @throws IOException if API call fails
 */
public static double[] downloadYahooAdjClose(
        String ticker,
        long startUnix,
        long endUnix,
        String interval
) throws IOException {

    String encodedTicker = URLEncoder.encode(ticker, StandardCharsets.UTF_8);

    String url = String.format(
            "https://query1.finance.yahoo.com/v8/finance/chart/%s" +
            "?period1=%d&period2=%d&interval=%s&events=div,splits",
            encodedTicker,
            startUnix,
            endUnix,
            interval
    );

    HttpURLConnection conn =
            (HttpURLConnection) new URL(url).openConnection();

    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
    conn.setConnectTimeout(15000);
    conn.setReadTimeout(15000);

    if (conn.getResponseCode() != 200) {
        throw new IOException("HTTP " + conn.getResponseCode());
    }

    String json;
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

        json = br.lines().collect(Collectors.joining());
    }

    JSONObject root = new JSONObject(json);
    JSONObject result =
            root.getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0);

    JSONArray adj =
            result.getJSONObject("indicators")
                  .getJSONArray("adjclose")
                  .getJSONObject(0)
                  .getJSONArray("adjclose");

    double[] prices = new double[adj.length()];

    for (int i = 0; i < adj.length(); i++) {
        if (adj.isNull(i)) {
            prices[i] = Double.NaN;
        } else {
            prices[i] = adj.getDouble(i);
        }
    }

    return prices;
}
    /**
     * Finds logarithmic returns {ln(ri+1/ri)} for the securites using their historic price data
     * @param rawData
     * @return logarithmic returns 
     */
    // calculating monthly retruns by finding 
    /* 
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
        */
    /**
     * calculates average monthly returns based on logarithmic returns
     * @param returnData
     * @return average monthly returns
     */
    /* 
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
        */
    /**
     * Calculates the variance of the securities based on logarithmic and average returns
     * @param returns
     * @param averageReturns
     * @return 2d Variance Matrix
     */
    /* 
    public static double[] CalculatingVariance(double returns[][], double averageReturns[]){
        //diff to find (R-average R) and sumSquaredDiff to find (R-average R)^2 and keep already calculated returns stored
        double VarianceTerms[] = new double[returns[0].length];
        double diff;
        
        for(int col = 0; col < returns[0].length; col++) {
            double sumSquaredDiff = 0.0;
            for(double[] i: returns){
                diff = i[col] - averageReturns[col];
                sumSquaredDiff += diff * diff;
                
            }
            VarianceTerms[col] = sumSquaredDiff / (returns.length - 1);
        }
        return VarianceTerms;
    }
        */
   /**
    * Calculates the standard deviaton of the securities using their variance
    * @param variance
    * @return standard deviation
    */
   /* 
    public static double[] CalculatingStockSTDV(double[] variance){
        double[] Sqrt = new double[variance.length];
        for(int i = 0; i < Sqrt.length; i++) {
            Sqrt[i] = Math.sqrt(variance[i]);
        }
        return Sqrt;
    }
        */

    /**
     * Calculates the covariance matrix for the securities. this is a requirement for calculating optimal portfolios later on
     * @param returns
     * @param means
     * @return covaraince matrix
     */
    /* 
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
        */
    /**
     * Extracts the covaraince terms from the covariance matrix
     * @param CoVar
     * @return covariance terms
     */
    public static double[] CovarianceTerms(double[][] CoVar){
        int size = CoVar.length;
        int index = 0;
        double [] subClassCovarianceTerms = new double[size* (size-1)];
        for(int collumn = 0; collumn < CoVar.length; collumn++){
            for(int row = 0; row < CoVar.length; row++){
                if(collumn == row) continue;
                subClassCovarianceTerms[index]  = CoVar[collumn][row];
                index++;
            }
        }
        return subClassCovarianceTerms;
    }
    /**
     * Calculates the inverse matrix using the ejml library
     * Calculated using an import library as covariance matrixes are singular and difficult to invert iteratively
     * Needed later to perform calculations for optimal portfolios
     * @param matrix
     * @return inverse matrix
     */
    /* 
    public static double[][] pseudoInverse(double[][] matrix) {
        DMatrixRMaj mat = new DMatrixRMaj(matrix);
        DMatrixRMaj pinv = new DMatrixRMaj(mat.numCols, mat.numRows);
        CommonOps_DDRM.pinv(mat, pinv);
        double[][] result = new double[pinv.numRows][pinv.numCols];
        for (int i = 0; i < pinv.numRows; i++) {
            for (int j = 0; j < pinv.numCols; j++) {
                result[i][j] = pinv.get(i,j);
            }
        }
        return result;
    }
    */
    /**
    * Calculates the Minimum Variance Portfolio, one of our optimal portfolios.
    * This portfolio has the combinstion of weights creatiging the lowest possible variance of our portfolio
    * @param covariance
    * @return weights of each security in the minimum variance portfolio
    */
   /* 
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
    */
    /**
     * Calculates the portfolios expected returns
     * @param EmonthlyReturns
     * @param Weights
     * @return Portfolio Expected Returns
     */
    /* 
    public static double CalculatingPortfolioReturn(double[] EmonthlyReturns, double[] Weights){
        int size = EmonthlyReturns.length;
        double ExpectedReturn = 0.0;
        for(int i = 0; i<size; i++){
            ExpectedReturn += (EmonthlyReturns[i]*Weights[i]);
        }
        double ExpectedReturnAsPercentage = (ExpectedReturn);
        return ExpectedReturnAsPercentage;
    }
        */
    /**
     * Calculates the portfolios variance
     * @param coVar
     * @param weights
     * @return Portfolio Variance
     */
    /* 
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
    */
    /**
     * Calculates the weights in the users current portfolio
     * with these weights we can calculate the portfolios metrics and compare them to our optimal portfolios
     * @param proportions
     * @return Weights of each security in the users portfolio
     */
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
    /**
     * Calculates the Tangency portfolio, one of our optimal portfolios
     * This portfolio has the highest return to risk ratio of any possible portfolio given the selected securities
     * @param inverse
     * @param ExpReturns
     * @param rf
     * @return unconstrained weights of each asset in the tangency portfolio
     */ 
    /* 
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
        */
    /**
     * Calculates the Sharpe Ratio for the tangency portfolio
     * This allows us to construct the capital allocation line
     * @param STDV
     * @param ExpReturn
     * @param Rf
     * @return Sharpe ratio
     */
    public static double CalculatingSharpeRatio(double STDV, double ExpReturn, double Rf){
        double sharpeRatio = ((ExpReturn-Rf)/ STDV);
        return sharpeRatio;
    }
    /**
     * Calculates a portfolios standard deviation
     * @param variance
     * @return standard deviation
     */
    /* 
    public static double CalculatingPortfolioSTDV(double variance){
        double stdv = Math.sqrt(variance);
        double stdvAsPercentage = stdv;
        return stdvAsPercentage;
    }
        */
    /**
     * Calculates standard deviation for the special case that the portfolio is on the capital allocation line 
     * @param TPStandardDeviation
     * @param weightInTP
     * @return standard deviation on the capital allocation line
     */
    public static double CalculatingSTDVonCAL(double TPStandardDeviation,double weightInTP){
        double stdv = (TPStandardDeviation*weightInTP);
        return stdv;
    }
    /**
     * Calculates Expected return for the special case that the portfolio is on the capital allocation line 
     * @param TPExpectedReturn
     * @param weightInTP
     * @return expected return on the capital allocation line
     */
    public static double CalculatingExpReturnonCAL(double TPExpectedReturn, double weightInTP){
        double expR = (weightInTP*TPExpectedReturn);
        return expR;
    }
   
    // calculating now for closed form markowitz scalars, moving away from ojalgo. at the minute
   // this method is one calculation away from finding efficient frontier weights for a given return
   //need to continue this to base against user portfolio and then manipulate formula to check against 
   //target STDV instead
   // not called in main yet
    /**
     * Calculates the weights for the portfolio on the efficient frontier for a user inputed level of standard deviation
     * This is one of our optimal portfolios, we can use the weights to calculate the optimal portfolios metrics
     * @param inverse
     * @param ExpReturns
     * @param R
     * @return weights of portfolio on the efficient frontier
     */
    /* 
    public static double[] CalculatingForGivenReturn(
        double [][] inverse,  
        double[] ExpReturns,
        double R
     ){
        int n = inverse.length;
        double A = 0.0, B = 0.0 , C = 0.0 ;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                A += inverse[i][j];
                B += inverse[i][j] * ExpReturns[j];
                C += ExpReturns[i] * inverse[i][j] * ExpReturns[j];
            }
        }
        double D = A * C - Math.pow(B, 2.0);
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
        //this method cannot be compared to ojalgo methods with constraints on weights.
        //can only be used against user portfolio
        return weights;
    }
    */

    // this method needs to be properly called in main 
    /**
     * Calculates the weights for the portfolio on the efficient frontier for a user inputed level of standard deviation
     * This is one of our optimal portfolios, we can use the weights to calculate the optimal portfolios metrics
     * @param inverse
     * @param ExpReturns
     * @param STDV
     * @return weights of portfolio on the efficient frontier
     */
    /* 
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
        if(var < 1/A){
            throw new IllegalArgumentException("portfolio cannot be on the efficient frontier give the variance is less than the MVP");
        }
        double discriminant = Math.pow(B, 2.0) - A*(C-(D*var));
        
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
        */
    
}