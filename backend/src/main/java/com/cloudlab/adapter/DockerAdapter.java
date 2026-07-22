package com.cloudlab.adapter;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.InvocationBuilder;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;

import jakarta.annotation.PreDestroy;

@Component
public class DockerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DockerAdapter.class);

    private final DockerClient client;
    private final boolean available;

    public DockerAdapter(CloudLabProperties properties) {
        DockerClient tmp = null;
        boolean ok = false;
        try {
            String host = properties.getIntegrations().getDockerHost();
            if (!StringUtils.hasText(host)) {
                host = "unix:///var/run/docker.sock";
            }
            log.info("Connecting Docker Engine at {}", host);
            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(host)
                    .withDockerTlsVerify(false)
                    .build();
            DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                    .dockerHost(URI.create(host))
                    .connectionTimeout(java.time.Duration.ofSeconds(5))
                    .responseTimeout(java.time.Duration.ofSeconds(30))
                    .build();
            tmp = DockerClientImpl.getInstance(config, httpClient);
            tmp.pingCmd().exec();
            ok = true;
            log.info("Docker engine connected: {}", host);
        } catch (Exception ex) {
            log.warn("Docker engine unavailable: {}", ex.toString());
        }
        this.client = tmp;
        this.available = ok;
    }

    @PreDestroy
    void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    public boolean isAvailable() {
        return available && client != null;
    }

    public List<Map<String, Object>> listContainers(boolean all) {
        requireClient();
        try {
            List<Container> containers = client.listContainersCmd().withShowAll(all).exec();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Container c : containers) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", c.getId());
                row.put("name", firstName(c.getNames()));
                row.put("image", c.getImage());
                row.put("state", c.getState());
                row.put("status", c.getStatus());
                row.put("ports", c.getPorts());
                row.put("created", c.getCreated());
                row.put("labels", c.getLabels());
                result.add(row);
            }
            return result;
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "list containers failed: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> inspect(String id) {
        requireClient();
        try {
            InspectContainerResponse inspect = client.inspectContainerCmd(id).exec();
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", inspect.getId());
            map.put("name", inspect.getName());
            map.put("image", inspect.getConfig() != null ? inspect.getConfig().getImage() : null);
            map.put("state", inspect.getState() != null ? inspect.getState().getStatus() : null);
            map.put("created", inspect.getCreated());
            return map;
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "inspect failed: " + ex.getMessage(), ex);
        }
    }

    public void start(String id) {
        requireClient();
        try {
            client.startContainerCmd(id).exec();
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "start failed: " + ex.getMessage(), ex);
        }
    }

    public void stop(String id) {
        requireClient();
        try {
            client.stopContainerCmd(id).exec();
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "stop failed: " + ex.getMessage(), ex);
        }
    }

    public void restart(String id) {
        requireClient();
        try {
            client.restartContainerCmd(id).exec();
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "restart failed: " + ex.getMessage(), ex);
        }
    }

    public void remove(String id, boolean force) {
        requireClient();
        try {
            client.removeContainerCmd(id).withForce(force).exec();
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "remove failed: " + ex.getMessage(), ex);
        }
    }

    public String logs(String id, int tail) {
        requireClient();
        try {
            StringBuilder sb = new StringBuilder();
            LogContainerCmd cmd = client.logContainerCmd(id)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTail(tail)
                    .withTimestamps(true);
            cmd.exec(new com.github.dockerjava.api.async.ResultCallback.Adapter<>() {
                @Override
                public void onNext(com.github.dockerjava.api.model.Frame item) {
                    sb.append(item.toString()).append('\n');
                }
            }).awaitCompletion(15, TimeUnit.SECONDS);
            return sb.toString();
        } catch (Exception ex) {
            throw new UpstreamException("DOCKER_ERROR", "logs failed: " + ex.getMessage(), ex);
        }
    }

    public Map<String, Object> statsSnapshot(String id) {
        requireClient();
        try {
            InvocationBuilder.AsyncResultCallback<Statistics> callback =
                    new InvocationBuilder.AsyncResultCallback<>();
            client.statsCmd(id).withNoStream(true).exec(callback);
            Statistics stats = callback.awaitResult();
            Map<String, Object> map = new LinkedHashMap<>();
            if (stats != null && stats.getMemoryStats() != null) {
                map.put("memoryUsage", stats.getMemoryStats().getUsage());
                map.put("memoryLimit", stats.getMemoryStats().getLimit());
            }
            return map;
        } catch (Exception ex) {
            log.debug("stats unavailable for {}: {}", id, ex.getMessage());
            return Map.of();
        }
    }

    private void requireClient() {
        if (!isAvailable()) {
            throw new UpstreamException("DOCKER_UNAVAILABLE", "Docker Engine is not reachable (mount docker.sock?)");
        }
    }

    private static String firstName(String[] names) {
        if (names == null || names.length == 0) {
            return "";
        }
        String n = names[0];
        return n.startsWith("/") ? n.substring(1) : n;
    }
}
