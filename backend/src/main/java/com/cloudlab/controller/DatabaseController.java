package com.cloudlab.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudlab.common.ApiResponse;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private final ObjectProvider<DataSource> dataSourceProvider;

    public DatabaseController(ObjectProvider<DataSource> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        Map<String, Object> map = new LinkedHashMap<>();
        DataSource ds = dataSourceProvider.getIfAvailable();
        if (ds == null) {
            map.put("available", false);
            map.put("message", "DataSource not configured (local profile)");
            return ApiResponse.ok(map);
        }
        try (var conn = ds.getConnection();
             var st = conn.createStatement()) {
            map.put("available", true);
            map.put("product", conn.getMetaData().getDatabaseProductName());
            map.put("version", conn.getMetaData().getDatabaseProductVersion());
            map.put("url", conn.getMetaData().getURL());
            try (var rs = st.executeQuery(
                    "SELECT pg_database_size(current_database()) AS size, "
                            + "(SELECT count(*) FROM pg_stat_activity) AS connections, "
                            + "(SELECT count(*) FROM information_schema.tables WHERE table_schema='public') AS tables")) {
                if (rs.next()) {
                    map.put("sizeBytes", rs.getLong("size"));
                    map.put("connections", rs.getInt("connections"));
                    map.put("tables", rs.getInt("tables"));
                }
            }
        } catch (Exception ex) {
            map.put("available", false);
            map.put("message", ex.getMessage());
        }
        return ApiResponse.ok(map);
    }
}
