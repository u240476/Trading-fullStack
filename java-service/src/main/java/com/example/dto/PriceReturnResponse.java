package com.example.dto;

import java.util.ArrayList;

public class PriceReturnResponse {

    private ArrayList<PricePoint> points;

    public PriceReturnResponse(ArrayList<PricePoint> points){
        this.points = points;
    }

    public ArrayList<PricePoint> getPoints(){
        return points;
    }
}
