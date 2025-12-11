package com.example;

import okhttp3.*;
import com.google.gson.*;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class KlineFetcher {
    private final Config cfg;
    private final OkHttpClient http;
    private final Gson gson = new Gson();
    private final ConcurrentHashMap<String, double[]> closesCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, double[]> lowsCache = new ConcurrentHashMap<>();

    public KlineFetcher(Config cfg) {
        this.cfg = cfg;
        this.http = new OkHttpClient.Builder()
            .build();
    }

    public double[] fetchCloses(String symbol) throws IOException {
        if (closesCache.containsKey(symbol)) return closesCache.get(symbol);
        String url = String.format("%s/fapi/v1/klines?symbol=%s&interval=%s&limit=%d",
                cfg.getBase(), symbol, cfg.getKlineInterval(), cfg.getKlineLimit());
        Request req = new Request.Builder().url(url).get().build();
        Response resp = http.newCall(req).execute();
        if (!resp.isSuccessful()) throw new IOException("klines failed: " + resp);
        String body = resp.body().string();
        JsonArray arr = gson.fromJson(body, JsonArray.class);
        int n = arr.size();
        double[] closes = new double[n];
        double[] lows = new double[n];
        for (int i = 0; i < n; i++) {
            JsonArray item = arr.get(i).getAsJsonArray();
            closes[i] = Double.parseDouble(item.get(4).getAsString());
            lows[i] = Double.parseDouble(item.get(3).getAsString());
        }
        closesCache.put(symbol, closes);
        lowsCache.put(symbol, lows);
        return closes;
    }

    public double[] fetchLowsFromCached(String symbol) throws IOException {
        if (lowsCache.containsKey(symbol)) return lowsCache.get(symbol);
        fetchCloses(symbol);
        return lowsCache.get(symbol);
    }
}
