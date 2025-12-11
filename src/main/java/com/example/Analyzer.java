package com.example;

import java.util.*;
/**
 * Heuristic:
 * - Maintain startIndex (valley) and currentPeak
 * - Iterate over time series:
 *   - if price > currentPeak -> update peak
 *   - if price <= currentPeak * 0.95 -> record sample (start->peak), then set new start at current index
 *   - if price < startPrice -> move start to current index and reset peak
 */
public class Analyzer {

    public Sample[] analyzeSymbol(String symbol, double[] closes, double[] lows) {
        if (closes == null || closes.length < 10) return null;
        List<Sample> samples = new ArrayList<>();
        int n = closes.length;
        int start = 0;
        double startPrice = closes[0];
        double peak = startPrice;
        for (int i = 1; i < n; i++) {
            double price = closes[i];
            if (price > peak) {
                peak = price;
            }
            if (price < startPrice) {
                start = i;
                startPrice = price;
                peak = price;
                continue;
            }
            double lowSince = lows[i];
            if (lowSince <= peak * 0.95) {
                double risePercent = (peak - startPrice) / startPrice * 100.0;
                Sample s = new Sample(symbol, startPrice, peak, risePercent);
                samples.add(s);
                start = i;
                startPrice = price;
                peak = price;
            }
        }
        return samples.toArray(new Sample[0]);
    }
}
