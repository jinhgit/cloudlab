package com.cloudlab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.adapter.GitHubActionsAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/deployments")
public class DeploymentsController {

    private final GitHubActionsAdapter github;

    public DeploymentsController(GitHubActionsAdapter github) {
        this.github = github;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(
            @RequestParam(defaultValue = "20") int perPage) {
        return ApiResponse.ok(Map.of(
                "configured", github.isConfigured(),
                "runs", github.listWorkflowRuns(perPage)
        ));
    }
}
