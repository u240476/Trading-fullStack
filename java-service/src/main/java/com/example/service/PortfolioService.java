package com.example.service;

import java.io.IOException;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.example.client.YahooFinanceClient;

@Service
public class PortfolioService {

    private final YahooFinanceClient yahooFinanceClient;

    public PortfolioService(YahooFinanceClient yahooFinanceClient) {
        this.yahooFinanceClient = yahooFinanceClient;
    }
    public double[][] getPriceMatrix(
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
                yahooFinanceClient.downloadYahooAdjClose(
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
