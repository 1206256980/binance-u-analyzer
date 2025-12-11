package com.example;

import java.util.Map;

public class AggregationResult {
    private final Map<String,Integer> buckets;
    private final int totalSamples;

    public AggregationResult(Map<String,Integer> buckets, int total) {
        this.buckets = buckets;
        this.totalSamples = total;
    }

    public Map<String,Integer> getBuckets(){ return buckets; }
    public int getTotalSamples(){ return totalSamples; }
}
