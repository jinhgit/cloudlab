package com.cloudlab.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final ObjectProvider<RedisConnectionFactory> redisFactory;

    public RedisController(ObjectProvider<RedisConnectionFactory> redisFactory) {
        this.redisFactory = redisFactory;
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        Map<String, Object> map = new LinkedHashMap<>();
        RedisConnectionFactory factory = redisFactory.getIfAvailable();
        if (factory == null) {
            map.put("available", false);
            map.put("message", "Redis not configured (local profile)");
            return ApiResponse.ok(map);
        }
        try (var conn = factory.getConnection()) {
            String pong = conn.ping();
            map.put("available", true);
            map.put("ping", pong);
            Properties info = conn.serverCommands().info();
            if (info != null) {
                map.put("redisVersion", info.getProperty("redis_version"));
                map.put("usedMemory", info.getProperty("used_memory_human"));
                map.put("connectedClients", info.getProperty("connected_clients"));
                map.put("keyspaceHits", info.getProperty("keyspace_hits"));
                map.put("keyspaceMisses", info.getProperty("keyspace_misses"));
                map.put("evictedKeys", info.getProperty("evicted_keys"));
            }
            Long dbSize = conn.serverCommands().dbSize();
            map.put("keyCount", dbSize);
        } catch (Exception ex) {
            map.put("available", false);
            map.put("message", ex.getMessage());
        }
        return ApiResponse.ok(map);
    }
}
