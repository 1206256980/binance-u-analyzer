package com.example;

import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeFetcher {
    private final Config cfg;
    private final OkHttpClient http;
    private final Gson gson = new Gson();

    public ExchangeFetcher(Config cfg) {
        this.cfg = cfg;
        this.http = new OkHttpClient.Builder()
            .build();
    }

    public String[] fetchAllPerpetualSymbols() throws IOException {
        String url = cfg.getBase() + "/fapi/v1/exchangeInfo";
        Request req = new Request.Builder().url(url).get().build();
        Response resp = http.newCall(req).execute();
        if (!resp.isSuccessful()) throw new IOException("exchangeInfo failed: " + resp);
        String body = resp.body().string();
        JsonObject root = gson.fromJson(body, JsonObject.class);
        JsonArray arr = root.getAsJsonArray("symbols");
        List<String> symbols = new ArrayList<>();
        for (JsonElement e : arr) {
            JsonObject o = e.getAsJsonObject();
            String status = o.has("status") ? o.get("status").getAsString() : "TRADING";
            if (!"TRADING".equals(status)) continue;
            if (o.has("contractType")) {
                String ct = o.get("contractType").getAsString();
                if (!"PERPETUAL".equalsIgnoreCase(ct)) continue;
            }
            String symbol = o.get("symbol").getAsString();
            if (!symbol.endsWith("USDT")) continue;
            symbols.add(symbol);
        }
        return symbols.toArray(new String[0]);
    }
}
