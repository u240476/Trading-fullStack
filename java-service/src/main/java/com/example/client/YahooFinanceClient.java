package com.example.client;
//NEEDS TO BE PROPERLY SPLIT UP NOT ALL THIS CODE SHOULD BE HERE
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class YahooFinanceClient {
    public static double[] downloadYahooAdjClose(
        String ticker,
        long startUnix,
        long endUnix,
        String interval
) throws IOException {

    String encodedTicker = URLEncoder.encode(ticker, StandardCharsets.UTF_8);

    String url = String.format(
            "https://query1.finance.yahoo.com/v8/finance/chart/%s" +
            "?period1=%d&period2=%d&interval=%s&events=div,splits",
            encodedTicker,
            startUnix,
            endUnix,
            interval
    );

    HttpURLConnection conn =
            (HttpURLConnection) new URL(url).openConnection();

    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
    conn.setConnectTimeout(15000);
    conn.setReadTimeout(15000);

    if (conn.getResponseCode() != 200) {
        throw new IOException("HTTP " + conn.getResponseCode());
    }

    String json;
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

        json = br.lines().collect(Collectors.joining());
    }

    JSONObject root = new JSONObject(json);
    JSONObject result =
            root.getJSONObject("chart")
                .getJSONArray("result")
                .getJSONObject(0);

    JSONArray adj =
            result.getJSONObject("indicators")
                  .getJSONArray("adjclose")
                  .getJSONObject(0)
                  .getJSONArray("adjclose");

    double[] prices = new double[adj.length()];

    for (int i = 0; i < adj.length(); i++) {
        if (adj.isNull(i)) {
            prices[i] = Double.NaN;
        } else {
            prices[i] = adj.getDouble(i);
        }
    }

    return prices;
}
}
