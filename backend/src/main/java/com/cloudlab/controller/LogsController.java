package com.cloudlab.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.adapter.LokiAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    private final LokiAdapter loki;

    public LogsController(LokiAdapter loki) {
        this.loki = loki;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> query(
            @RequestParam(defaultValue = "{compose_project=\"cloudlab\"}") String query,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "3600") long rangeSeconds) {
        long endNs = Instant.now().toEpochMilli() * 1_000_000L;
        long startNs = endNs - rangeSeconds * 1_000_000_000L;
        return ApiResponse.ok(loki.queryRange(query, startNs, endNs, limit));
    }

    @GetMapping("/labels")
    public ApiResponse<Map<String, Object>> labels() {
        return ApiResponse.ok(loki.labels());
    }
}
