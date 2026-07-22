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

import com.cloudlab.adapter.DockerAdapter;
import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    private final DockerAdapter docker;

    public DockerController(DockerAdapter docker) {
        this.docker = docker;
    }

    @GetMapping("/containers")
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(defaultValue = "true") boolean all) {
        return ApiResponse.ok(docker.listContainers(all));
    }

    @GetMapping("/containers/{id}")
    public ApiResponse<Map<String, Object>> inspect(@PathVariable String id) {
        return ApiResponse.ok(docker.inspect(id));
    }

    @GetMapping("/containers/{id}/logs")
    public ApiResponse<Map<String, String>> logs(
            @PathVariable String id,
            @RequestParam(defaultValue = "200") int tail) {
        return ApiResponse.ok(Map.of("logs", docker.logs(id, tail)));
    }

    @GetMapping("/containers/{id}/stats")
    public ApiResponse<Map<String, Object>> stats(@PathVariable String id) {
        return ApiResponse.ok(docker.statsSnapshot(id));
    }

    @PostMapping("/containers/{id}/start")
    public ApiResponse<Map<String, String>> start(@PathVariable String id) {
        docker.start(id);
        return ApiResponse.ok(Map.of("status", "started", "id", id));
    }

    @PostMapping("/containers/{id}/stop")
    public ApiResponse<Map<String, String>> stop(@PathVariable String id) {
        docker.stop(id);
        return ApiResponse.ok(Map.of("status", "stopped", "id", id));
    }

    @PostMapping("/containers/{id}/restart")
    public ApiResponse<Map<String, String>> restart(@PathVariable String id) {
        docker.restart(id);
        return ApiResponse.ok(Map.of("status", "restarted", "id", id));
    }

    @DeleteMapping("/containers/{id}")
    public ApiResponse<Map<String, String>> remove(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean force) {
        docker.remove(id, force);
        return ApiResponse.ok(Map.of("status", "removed", "id", id));
    }
}
