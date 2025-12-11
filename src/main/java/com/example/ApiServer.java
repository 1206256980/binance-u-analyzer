package com.example;

import static spark.Spark.*;
import com.google.gson.Gson;

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
            return gson.toJson(new java.util.HashMap<String,Object>() {{
                put("buckets", r.getBuckets());
                put("totalSamples", r.getTotalSamples());
                put("updatedAt", System.currentTimeMillis());
            }});
        });
        get("/health", (req,res) -> "ok");
    }
}
