package com.example;

import com.google.gson.Gson;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        initProxy();
        Config cfg = Config.load("app.properties");
        CacheService cache = new CacheService(cfg);
        ExchangeFetcher exchangeFetcher = new ExchangeFetcher(cfg);
        KlineFetcher kf = new KlineFetcher(cfg);
        Analyzer analyzer = new Analyzer();

        // Executor
        int pool = cfg.getThreadPoolSize();
        ExecutorService exec = Executors.newFixedThreadPool(pool);

        // Start API server
        ApiServer api = new ApiServer(cfg, cache);
        api.start();

        // Schedule periodic job (every 5 minutes default - we run every 5 minutes)
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final Runnable job = () -> {
            try {
                System.out.println("Job started: fetch symbols");
                String[] symbols = exchangeFetcher.fetchAllPerpetualSymbols();
                System.out.println("Symbols count: " + symbols.length);
                ConcurrentLinkedQueue<Sample> samplesQ = new ConcurrentLinkedQueue<>();

                CountDownLatch latch = new CountDownLatch(symbols.length);
                for (String s : symbols) {
                    exec.submit(() -> {
                        try {
                            double[] closes = kf.fetchCloses(s);
                            double[] lows = kf.fetchLowsFromCached(s);
                            long[] times = kf.fetchTimes(s);     // <----- 新增
                            Sample[] samples = analyzer.analyzeSymbol(s, closes, lows, times);
                            if (samples != null) {
                                for (Sample sam : samples) samplesQ.add(sam);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing " + s + " : " + e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    });
                    try { Thread.sleep(cfg.getPauseMsPerReq()); } catch (InterruptedException ignored){}
                }
                latch.await();

                // Aggregate
                Aggregator agg = new Aggregator();
                AggregationResult result = agg.aggregate(samplesQ);

                // Cache result
                cache.putAggregated(result);

                System.out.println("Job finished: samples=" + new Gson().toJson(result));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(job, 0, 5, TimeUnit.MINUTES);
    }

    private static void initProxy() {
        String isProxy = System.getenv("is_proxy");
        System.out.println("当前代理状态："+isProxy);
        if ("false".equals(isProxy)) {
            return;
        }
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");
    }
}
