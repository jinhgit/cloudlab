package com.cloudlab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudlab.adapter.AlertmanagerAdapter;
import com.cloudlab.adapter.DockerAdapter;
import com.cloudlab.adapter.KubernetesAdapter;
import com.cloudlab.adapter.LokiAdapter;
import com.cloudlab.adapter.PrometheusAdapter;

@ExtendWith(MockitoExtension.class)
class PlatformOverviewServiceTest {

    @Mock
    PrometheusAdapter prometheus;
    @Mock
    DockerAdapter docker;
    @Mock
    KubernetesAdapter kubernetes;
    @Mock
    LokiAdapter loki;
    @Mock
    AlertmanagerAdapter alertmanager;

    PlatformOverviewService service;

    @BeforeEach
    void setUp() {
        service = new PlatformOverviewService(prometheus, docker, kubernetes, loki, alertmanager);
    }

    @Test
    void aggregatesUpstreamStatusAndMetrics() {
        when(prometheus.ready()).thenReturn(true);
        when(loki.ready()).thenReturn(true);
        when(alertmanager.ready()).thenReturn(false);
        when(docker.isAvailable()).thenReturn(true);
        when(kubernetes.isAvailable()).thenReturn(false);
        when(prometheus.scalarOrZero(anyString())).thenReturn(12.5);
        when(docker.listContainers(anyBoolean())).thenReturn(List.of(
                Map.of("state", "running"),
                Map.of("state", "exited"),
                Map.of("state", "running")
        ));
        when(alertmanager.alerts()).thenReturn(List.of(
                Map.of("status", Map.of("state", "firing")),
                Map.of("status", Map.of("state", "resolved"))
        ));

        Map<String, Object> status = service.serverStatus();

        assertThat(status.get("prometheus")).isEqualTo(true);
        assertThat(status.get("loki")).isEqualTo(true);
        assertThat(status.get("alertmanager")).isEqualTo(false);
        assertThat(status.get("docker")).isEqualTo(true);
        assertThat(status.get("kubernetes")).isEqualTo(false);
        assertThat(status.get("containersRunning")).isEqualTo(2);
        assertThat(status.get("containersTotal")).isEqualTo(3);
        assertThat(status.get("alertsFiring")).isEqualTo(1);
        assertThat(status.get("cpuPercent")).isEqualTo(12.5);
    }
}
