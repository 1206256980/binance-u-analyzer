package com.example;

import java.util.*;

public class Aggregator {

    public AggregationResult aggregate(Iterable<Sample> samples) {

        // ------------ ① 动态区间定义（想改随时改） ---------------
        // 下面表示：0-4、4-6、6-8、8-10、10+
        int[] bounds = {0, 4, 6, 8, 10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,66,68,70,72,74,76,78,80,82,84,86,88,90,92,94,96,98,100};
        // --------------------------------------------------------

        // 自动创建区间 bucket
        Map<String, Integer> buckets = new LinkedHashMap<>();
        for (int i = 0; i < bounds.length - 1; i++) {
            buckets.put(bounds[i] + "-" + bounds[i + 1], 0);
        }
        buckets.put(bounds[bounds.length - 1] + "+", 0); // 最后一段：10+

        int total = 0;

        for (Sample s : samples) {
            total++;
            double r = s.risePercent;
            boolean matched = false;

            // 找到所属区间
            for (int i = 0; i < bounds.length - 1; i++) {
                if (r >= bounds[i] && r < bounds[i + 1]) {
                    String key = bounds[i] + "-" + bounds[i + 1];
                    buckets.put(key, buckets.get(key) + 1);
                    matched = true;
                    break;
                }
            }

            // 超出最后一个区间
            if (!matched && r >= bounds[bounds.length - 1]) {
                String key = bounds[bounds.length - 1] + "+";
                buckets.put(key, buckets.get(key) + 1);
            }
        }

        return new AggregationResult(buckets, total);
    }

}
