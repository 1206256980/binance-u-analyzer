package com.example;

import java.io.*;
import java.util.Properties;

public class Config {
    private Properties p = new Properties();
    public static Config load(String path) {
        try {
            InputStream in = Config.class.getClassLoader().getResourceAsStream(path);
            Properties props = new Properties();
            props.load(in);
            Config c = new Config();
            c.p = props;
            return c;
        } catch (Exception e) {
            throw new RuntimeException("Failed load config: " + e.getMessage(), e);
        }
    }
    public String getBase(){ return p.getProperty("binance.api.base","https://fapi.binance.com"); }
    public int getKlineLimit(){ return Integer.parseInt(p.getProperty("klines.limit","1000")); }
    public String getKlineInterval(){ return p.getProperty("kline.interval","5m"); }
    public int getThreadPoolSize(){ return Integer.parseInt(p.getProperty("thread.pool.size","40")); }
    public int getServerPort(){ return Integer.parseInt(p.getProperty("server.port","4567")); }
    public int getPauseMsPerReq(){ return Integer.parseInt(p.getProperty("pause.ms.per.req","50")); }
    public String getRedisHost(){ return p.getProperty("redis.host",""); }
    public int getRedisPort(){ return Integer.parseInt(p.getProperty("redis.port","6379")); }
    public String getRedisPrefix(){ return p.getProperty("redis.prefix","binance_u_analyzer:"); }
    public int getCacheTtl(){ return Integer.parseInt(p.getProperty("cache.ttl.seconds","600")); }
}
