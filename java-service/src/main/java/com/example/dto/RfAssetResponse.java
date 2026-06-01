package com.example.dto;

public class RfAssetResponse {
    private double rfAssetYield;

    public RfAssetResponse(double rfAssetYield){
        this.rfAssetYield = rfAssetYield;
    }

    public double getRfAssetYield(){
        return rfAssetYield;
    }
}
