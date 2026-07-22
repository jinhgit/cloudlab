package com.cloudlab.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Value("${spring.application.name:cloudlab-backend}")
    private String applicationName;

    @Value("${cloudlab.version:0.1.0-SNAPSHOT}")
    private String version;

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of(
                "status", "UP",
                "service", applicationName,
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/platform/info")
    public ApiResponse<Map<String, Object>> platformInfo() {
        return ApiResponse.ok(Map.of(
                "name", "CloudLab Platform API",
                "version", version,
                "description", "Self-hosted DevOps operations control plane"
        ));
    }
}
