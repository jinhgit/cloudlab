package com.cloudlab.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.common.ApiResponse;
import com.cloudlab.service.PlatformOverviewService;

@RestController
@RequestMapping("/api/server")
public class ServerController {

    private final PlatformOverviewService overviewService;

    public ServerController(PlatformOverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        return ApiResponse.ok(overviewService.serverStatus());
    }
}
