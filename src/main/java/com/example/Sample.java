package com.example;

public class Sample {
    public final String symbol;
    public final double startPrice;
    public final double peakPrice;
    public final double risePercent;

    public Sample(String symbol, double startPrice, double peakPrice, double risePercent) {
        this.symbol = symbol;
        this.startPrice = startPrice;
        this.peakPrice = peakPrice;
        this.risePercent = risePercent;
    }
}
