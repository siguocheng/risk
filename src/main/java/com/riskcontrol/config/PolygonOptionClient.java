package com.riskcontrol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PolygonOptionClient {

    private final OkHttpClient client;
    private final PolygonConfig polygonConfig;

    private final ObjectMapper mapper = new ObjectMapper();

    public String getOptionSnapshot(String symbol) throws IOException {

        HttpUrl url = HttpUrl.parse(
                polygonConfig.getBaseUrl()
                        + "/v3/snapshot/options/"
                        + symbol)
                .newBuilder()
                .addQueryParameter("apiKey", polygonConfig.getApiKey())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException(response.body().string());
            }

            return response.body().string();
        }
    }
}
