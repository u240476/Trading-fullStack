package com.example.cache;

public class Stock {

    private final double[] priceData;

    public static class Builder {

        private final double[] priceData;

        public Builder(double[] priceData) {
            this.priceData = priceData;
        }

        public Stock build() {
            return new Stock(this);
        }
    }

    private Stock(Builder builder) {
        this.priceData = builder.priceData;
    }

    public static Stock of(double[] priceData) {
        return new Builder(priceData).build();
    }

    public double[] getPriceData() {
        return priceData;
    }
}