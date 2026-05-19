package com.example.dto;

public class PricePoint {
    private double price;
    private int time;

    public PricePoint(double price, int time) {
        this.price = price;
        this.time = time;
    }

    public double getPrice() { return price; }
    public int getTime() { return time; }
}