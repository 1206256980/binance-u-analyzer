package com.example;

import redis.clients.jedis.Jedis;
import com.google.gson.Gson;

public class CacheService {
    private final Config cfg;
    private final Gson gson = new Gson();
    private AggregationResult last;
    private Jedis jedis;

    public CacheService(Config cfg) {
        this.cfg = cfg;
        if (cfg.getRedisHost() != null && !cfg.getRedisHost().isEmpty()) {
            try {
                jedis = new Jedis(cfg.getRedisHost(), cfg.getRedisPort());
                System.out.println("Using Redis cache at " + cfg.getRedisHost());
            } catch (Exception e) {
                System.err.println("Cannot connect to redis, fallback to memory: " + e.getMessage());
                jedis = null;
            }
        }
    }

    public void putAggregated(AggregationResult res) {
        this.last = res;
        if (jedis != null) {
            String json = gson.toJson(res.getBuckets());
            jedis.setex(cfg.getRedisPrefix() + "aggregated", cfg.getCacheTtl(), json);
        }
    }

    public AggregationResult getAggregated() {
        if (last != null) return last;
        if (jedis != null) {
            String json = jedis.get(cfg.getRedisPrefix() + "aggregated");
            if (json != null) {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.Map<String,Integer>>(){}.getType();
                java.util.Map<String,Integer> map = gson.fromJson(json, type);
                return new AggregationResult(map, map.values().stream().mapToInt(Integer::intValue).sum());
            }
        }
        return new AggregationResult(new java.util.LinkedHashMap<String,Integer>(){{put("0-4",0);put("4-6",0);put("6-8",0);put("8-10",0);put("10+",0);}},0);
    }
}
