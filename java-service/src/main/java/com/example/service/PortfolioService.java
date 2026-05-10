package com.example.service;

import java.io.IOException;
import java.util.Calendar;
import com.example.client.YahooFinanceClient;

public class PortfolioService {
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
                YahooFinanceClient.downloadYahooAdjClose(
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


}
