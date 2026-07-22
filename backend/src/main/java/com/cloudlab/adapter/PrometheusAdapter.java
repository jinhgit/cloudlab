package com.cloudlab.adapter;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PrometheusAdapter {

    private final RestClient client;
    private final String baseUrl;

    public PrometheusAdapter(RestClient.Builder builder, CloudLabProperties properties) {
        this.baseUrl = trimSlash(properties.getIntegrations().getPrometheusUrl());
        this.client = builder.build();
    }

    public Map<String, Object> query(String promql) {
        try {
            URI uri = URI.create(baseUrl + "/api/v1/query?query=" + enc(promql));
            JsonNode root = client.get().uri(uri).retrieve().body(JsonNode.class);
            return toMap(root);
        } catch (RestClientException ex) {
            throw new UpstreamException("PROMETHEUS_UNAVAILABLE", "Prometheus query failed: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> queryRange(String promql, long start, long end, String step) {
        try {
            String q = baseUrl + "/api/v1/query_range?query=" + enc(promql)
                    + "&start=" + start + "&end=" + end + "&step=" + enc(step);
            JsonNode root = client.get().uri(URI.create(q)).retrieve().body(JsonNode.class);
            return toMap(root);
        } catch (RestClientException ex) {
            throw new UpstreamException("PROMETHEUS_UNAVAILABLE", "Prometheus query_range failed: " + ex.getMessage(), ex);
        }
    }

    public double scalarOrZero(String promql) {
        Map<String, Object> result = query(promql);
        try {
            Object dataObj = result.get("data");
            if (!(dataObj instanceof JsonNode data)) {
                return 0;
            }
            JsonNode list = data.path("result");
            if (!list.isArray() || list.isEmpty()) {
                return 0;
            }
            JsonNode value = list.get(0).path("value");
            if (!value.isArray() || value.size() < 2) {
                return 0;
            }
            return Double.parseDouble(value.get(1).asText());
        } catch (Exception ignored) {
            return 0;
        }
    }

    public boolean ready() {
        try {
            String body = client.get().uri(URI.create(baseUrl + "/-/ready")).retrieve().body(String.class);
            return body != null && body.toLowerCase().contains("ready");
        } catch (Exception ex) {
            return false;
        }
    }

    private Map<String, Object> toMap(JsonNode root) {
        if (root == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", root.path("status").asText());
        map.put("data", root.path("data"));
        return map;
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
