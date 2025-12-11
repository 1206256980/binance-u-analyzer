package com.example;

import java.util.*;

public class Aggregator {

    public AggregationResult aggregate(Iterable<Sample> samples) {
        Map<String, Integer> buckets = new LinkedHashMap<>();
        buckets.put("0-4", 0);
        buckets.put("4-6", 0);
        buckets.put("6-8", 0);
        buckets.put("8-10", 0);
        buckets.put("10+", 0);

        int total = 0;
        for (Sample s : samples) {
            total++;
            double r = s.risePercent;
            if (r < 4.0) {
                buckets.put("0-4", buckets.get("0-4") + 1);
            } else if (r < 6.0) {
                buckets.put("4-6", buckets.get("4-6") + 1);
            } else if (r < 8.0) {
                buckets.put("6-8", buckets.get("6-8") + 1);
            } else if (r < 10.0) {
                buckets.put("8-10", buckets.get("8-10") + 1);
            } else {
                buckets.put("10+", buckets.get("10+") + 1);
            }
        }
        return new AggregationResult(buckets, total);
    }
}
