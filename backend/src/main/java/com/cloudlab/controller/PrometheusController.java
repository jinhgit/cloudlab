package com.cloudlab.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.adapter.PrometheusAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/prometheus")
public class PrometheusController {

    private final PrometheusAdapter prometheus;

    public PrometheusController(PrometheusAdapter prometheus) {
        this.prometheus = prometheus;
    }

    @GetMapping("/query")
    public ApiResponse<Map<String, Object>> query(@RequestParam String query) {
        return ApiResponse.ok(prometheus.query(query));
    }

    @GetMapping("/query_range")
    public ApiResponse<Map<String, Object>> queryRange(
            @RequestParam String query,
            @RequestParam(required = false) Long start,
            @RequestParam(required = false) Long end,
            @RequestParam(defaultValue = "15s") String step) {
        long endSec = end != null ? end : Instant.now().getEpochSecond();
        long startSec = start != null ? start : endSec - 3600;
        return ApiResponse.ok(prometheus.queryRange(query, startSec, endSec, step));
    }

    @GetMapping("/cpu")
    public ApiResponse<Map<String, Object>> cpu(
            @RequestParam(defaultValue = "3600") long rangeSeconds) {
        return range("100 - (avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)", rangeSeconds);
    }

    @GetMapping("/memory")
    public ApiResponse<Map<String, Object>> memory(
            @RequestParam(defaultValue = "3600") long rangeSeconds) {
        return range("(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100", rangeSeconds);
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cpu", prometheus.scalarOrZero(
                "100 - (avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)"));
        map.put("memory", prometheus.scalarOrZero(
                "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100"));
        map.put("jvmHeapUsed", prometheus.scalarOrZero(
                "sum(jvm_memory_used_bytes{area=\"heap\",job=\"cloudlab-backend\"})"));
        map.put("httpRps", prometheus.scalarOrZero(
                "sum(rate(http_server_requests_seconds_count{job=\"cloudlab-backend\"}[1m]))"));
        map.put("ready", prometheus.ready());
        return ApiResponse.ok(map);
    }

    private ApiResponse<Map<String, Object>> range(String promql, long rangeSeconds) {
        long end = Instant.now().getEpochSecond();
        long start = end - rangeSeconds;
        return ApiResponse.ok(prometheus.queryRange(promql, start, end, "30s"));
    }
}
