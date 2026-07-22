package com.cloudlab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.adapter.AlertmanagerAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    private final AlertmanagerAdapter alertmanager;

    public AlertsController(AlertmanagerAdapter alertmanager) {
        this.alertmanager = alertmanager;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.ok(alertmanager.alerts());
    }
}
