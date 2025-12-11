package com.example;

import static spark.Spark.*;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ApiServer {
    private final Config cfg;
    private final CacheService cache;
    private final Gson gson = new Gson();

    public ApiServer(Config cfg, CacheService cache) {
        this.cfg = cfg;
        this.cache = cache;
    }

    public void start() {
        port(cfg.getServerPort());
        get("/stats", (req, res) -> {
            res.type("application/json");
            AggregationResult r = cache.getAggregated();
            System.out.println(new Gson().toJson(r));
            Map<String, Object> map = new HashMap<>();
            map.put("buckets", r.getBuckets());
            map.put("totalSamples", r.getTotalSamples());
            map.put("updatedAt", System.currentTimeMillis());

            return gson.toJson(map);
        });
        get("/health", (req,res) -> "ok");
    }
}
