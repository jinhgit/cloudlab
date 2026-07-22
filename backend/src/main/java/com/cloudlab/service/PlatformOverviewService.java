package com.cloudlab.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cloudlab.adapter.DockerAdapter;
import com.cloudlab.adapter.KubernetesAdapter;
import com.cloudlab.adapter.LokiAdapter;
import com.cloudlab.adapter.PrometheusAdapter;
import com.cloudlab.adapter.AlertmanagerAdapter;

@Service
public class PlatformOverviewService {

    private final PrometheusAdapter prometheus;
    private final DockerAdapter docker;
    private final KubernetesAdapter kubernetes;
    private final LokiAdapter loki;
    private final AlertmanagerAdapter alertmanager;

    public PlatformOverviewService(
            PrometheusAdapter prometheus,
            DockerAdapter docker,
            KubernetesAdapter kubernetes,
            LokiAdapter loki,
            AlertmanagerAdapter alertmanager) {
        this.prometheus = prometheus;
        this.docker = docker;
        this.kubernetes = kubernetes;
        this.loki = loki;
        this.alertmanager = alertmanager;
    }

    public Map<String, Object> serverStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("prometheus", prometheus.ready());
        status.put("loki", loki.ready());
        status.put("alertmanager", alertmanager.ready());
        status.put("docker", docker.isAvailable());
        status.put("kubernetes", kubernetes.isAvailable());

        double cpu = prometheus.scalarOrZero(
                "100 - (avg(rate(node_cpu_seconds_total{mode=\"idle\"}[5m])) * 100)");
        double mem = prometheus.scalarOrZero(
                "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100");
        double disk = prometheus.scalarOrZero(
                "(1 - (node_filesystem_avail_bytes{mountpoint=\"/\"} / node_filesystem_size_bytes{mountpoint=\"/\"})) * 100");

        status.put("cpuPercent", round(cpu));
        status.put("memoryPercent", round(mem));
        status.put("diskPercent", round(disk));

        int running = 0;
        int total = 0;
        if (docker.isAvailable()) {
            try {
                List<Map<String, Object>> containers = docker.listContainers(true);
                total = containers.size();
                running = (int) containers.stream()
                        .filter(c -> "running".equalsIgnoreCase(String.valueOf(c.get("state"))))
                        .count();
            } catch (Exception ignored) {
                // leave zeros
            }
        }
        status.put("containersRunning", running);
        status.put("containersTotal", total);

        int pods = 0;
        if (kubernetes.isAvailable()) {
            try {
                pods = kubernetes.listPods("all").size();
            } catch (Exception ignored) {
                // leave zero
            }
        }
        status.put("podsTotal", pods);

        int alerts = 0;
        try {
            alerts = (int) alertmanager.alerts().stream()
                    .filter(a -> {
                        Object statusMap = a.get("status");
                        if (statusMap instanceof Map<?, ?> m) {
                            return "firing".equals(String.valueOf(m.get("state")));
                        }
                        return true;
                    })
                    .count();
        } catch (Exception ignored) {
            // leave zero
        }
        status.put("alertsFiring", alerts);

        status.put("jvmHeapUsed", prometheus.scalarOrZero(
                "sum(jvm_memory_used_bytes{area=\"heap\",job=\"cloudlab-backend\"})"));
        status.put("jvmHeapMax", prometheus.scalarOrZero(
                "sum(jvm_memory_max_bytes{area=\"heap\",job=\"cloudlab-backend\"})"));

        return status;
    }

    private static double round(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return 0;
        }
        return Math.round(v * 10.0) / 10.0;
    }
}
