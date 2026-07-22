package com.cloudlab.adapter;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class LokiAdapter {

    private final RestClient client;
    private final String baseUrl;

    public LokiAdapter(RestClient.Builder builder, CloudLabProperties properties) {
        this.baseUrl = trimSlash(properties.getIntegrations().getLokiUrl());
        this.client = builder.build();
    }

    public Map<String, Object> queryRange(String logql, long startNs, long endNs, int limit) {
        try {
            String q = baseUrl + "/loki/api/v1/query_range"
                    + "?query=" + enc(logql)
                    + "&start=" + startNs
                    + "&end=" + endNs
                    + "&limit=" + limit
                    + "&direction=backward";
            JsonNode root = client.get().uri(URI.create(q)).retrieve().body(JsonNode.class);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", root != null ? root.path("status").asText() : "error");
            map.put("data", root != null ? root.path("data") : null);
            return map;
        } catch (RestClientException ex) {
            throw new UpstreamException("LOKI_UNAVAILABLE", "Loki query failed: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> labels() {
        try {
            JsonNode root = client.get().uri(URI.create(baseUrl + "/loki/api/v1/labels")).retrieve().body(JsonNode.class);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("status", root != null ? root.path("status").asText() : "error");
            map.put("data", root != null ? root.path("data") : Collections.emptyList());
            return map;
        } catch (RestClientException ex) {
            throw new UpstreamException("LOKI_UNAVAILABLE", "Loki labels failed: " + ex.getMessage(), ex);
        }
    }

    public boolean ready() {
        try {
            String body = client.get().uri(URI.create(baseUrl + "/ready")).retrieve().body(String.class);
            return body != null && body.toLowerCase().contains("ready");
        } catch (Exception ex) {
            return false;
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String trimSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
