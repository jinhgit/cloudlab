package com.cloudlab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.adapter.KubernetesAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/kubernetes")
public class KubernetesController {

    private final KubernetesAdapter kubernetes;

    public KubernetesController(KubernetesAdapter kubernetes) {
        this.kubernetes = kubernetes;
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        return ApiResponse.ok(Map.of("available", kubernetes.isAvailable()));
    }

    @GetMapping("/pods")
    public ApiResponse<List<Map<String, Object>>> pods(
            @RequestParam(defaultValue = "all") String namespace) {
        return ApiResponse.ok(kubernetes.listPods(namespace));
    }

    @GetMapping("/deployments")
    public ApiResponse<List<Map<String, Object>>> deployments(
            @RequestParam(defaultValue = "all") String namespace) {
        return ApiResponse.ok(kubernetes.listDeployments(namespace));
    }

    @DeleteMapping("/pods/{namespace}/{name}")
    public ApiResponse<Map<String, String>> deletePod(
            @PathVariable String namespace,
            @PathVariable String name) {
        kubernetes.deletePod(namespace, name);
        return ApiResponse.ok(Map.of("status", "deleted", "name", name, "namespace", namespace));
    }

    @PostMapping("/deployments/{namespace}/{name}/restart")
    public ApiResponse<Map<String, String>> restartDeployment(
            @PathVariable String namespace,
            @PathVariable String name) {
        kubernetes.restartDeployment(namespace, name);
        return ApiResponse.ok(Map.of("status", "restarting", "name", name, "namespace", namespace));
    }
}
