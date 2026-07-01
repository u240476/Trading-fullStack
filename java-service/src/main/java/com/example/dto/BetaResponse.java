package com.example.dto;

import java.util.List;

public class BetaResponse {

    private List<String> tickers;
    private List<Double> beta;

    public BetaResponse(List<String> tickers, List<Double> beta) {
        this.tickers = tickers;
        this.beta = beta;
    }

    public List<String> getTickers() {
        return tickers;
    }

    public List<Double> getBeta() {
        return beta;
    }
}