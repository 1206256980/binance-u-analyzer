package com.example;

import java.time.Instant;
import java.time.ZoneId;
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

    public Sample[] analyzeSymbol(String symbol, double[] closes, double[] lows, long[] times) {
        if (closes == null || closes.length < 10) return null;

        List<Sample> samples = new ArrayList<>();
        int n = closes.length;

        int start = 0;
        double startPrice = closes[0];
        double peak = startPrice;

        for (int i = 1; i < n; i++) {
            double price = closes[i];

            // 创新高
            if (price > peak) {
                peak = price;
            }

            // 跌破起点 → 重置上涨段
            if (price < startPrice) {
                start = i;
                startPrice = price;
                peak = price;
                continue;
            }

            double lowSince = lows[i];

            // 回落≥5%
            if (lowSince <= peak * 0.95) {

                double risePercent = (peak - startPrice) / startPrice * 100.0;
                double drawdownPct = (peak - lowSince) / peak * 100.0;

                // ------------ 新增：找到第一个触发回落≥5% 的时间点 ------------
                int dropIndex = i;               // 第一次出现≥5%回落的 K 线下标
                long dropTime = times[dropIndex]; // 该 K 线的时间戳（毫秒）
                String dropTimeStr = Instant.ofEpochMilli(dropTime)
                        .atZone(ZoneId.of("UTC+8"))
                        .toLocalDateTime()
                        .toString();
                // -------------------------------------------------------------------

                // 打印信息（包含开始回落的时间点）
                if (risePercent >= 10.0) {
                    System.out.println(
                            "[回落>=5%] " + symbol +
                                    " 涨幅=" + String.format("%.2f", risePercent) + "%" +
                                    " 回落=" + String.format("%.2f", drawdownPct) + "%" +
                                    " 回落时间=" + dropTimeStr
                    );
                }

                // 保存 sample
                samples.add(new Sample(symbol, startPrice, peak, risePercent));

                // 开始下一段
                start = i;
                startPrice = price;
                peak = price;
            }
        }

        return samples.toArray(new Sample[0]);
    }

}
