package com.cloudlab.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;

@Component
public class KubernetesAdapter {

    private static final Logger log = LoggerFactory.getLogger(KubernetesAdapter.class);

    private final CoreV1Api coreApi;
    private final AppsV1Api appsApi;
    private final boolean available;

    public KubernetesAdapter(CloudLabProperties properties) {
        CoreV1Api core = null;
        AppsV1Api apps = null;
        boolean ok = false;
        try {
            ApiClient client;
            String kubeconfig = properties.getIntegrations().getKubeconfig();
            if (StringUtils.hasText(kubeconfig)) {
                client = Config.fromConfig(kubeconfig);
            } else {
                client = Config.defaultClient();
            }
            io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
            core = new CoreV1Api(client);
            apps = new AppsV1Api(client);
            core.listNamespace().execute();
            ok = true;
            log.info("Kubernetes API connected");
        } catch (Exception ex) {
            log.warn("Kubernetes API unavailable: {}", ex.getMessage());
        }
        this.coreApi = core;
        this.appsApi = apps;
        this.available = ok;
    }

    public boolean isAvailable() {
        return available && coreApi != null;
    }

    public List<Map<String, Object>> listPods(String namespace) {
        requireClient();
        try {
            V1PodList list;
            if (StringUtils.hasText(namespace) && !namespace.equals("*") && !namespace.equals("all")) {
                list = coreApi.listNamespacedPod(namespace).execute();
            } else {
                list = coreApi.listPodForAllNamespaces().execute();
            }
            List<Map<String, Object>> rows = new ArrayList<>();
            if (list.getItems() == null) {
                return rows;
            }
            for (V1Pod pod : list.getItems()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", pod.getMetadata() != null ? pod.getMetadata().getName() : null);
                row.put("namespace", pod.getMetadata() != null ? pod.getMetadata().getNamespace() : null);
                row.put("phase", pod.getStatus() != null ? pod.getStatus().getPhase() : null);
                row.put("node", pod.getSpec() != null ? pod.getSpec().getNodeName() : null);
                row.put("startTime", pod.getStatus() != null ? pod.getStatus().getStartTime() : null);
                int restarts = 0;
                if (pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null) {
                    for (V1ContainerStatus cs : pod.getStatus().getContainerStatuses()) {
                        restarts += cs.getRestartCount() != null ? cs.getRestartCount() : 0;
                    }
                }
                row.put("restarts", restarts);
                List<String> images = new ArrayList<>();
                if (pod.getSpec() != null && pod.getSpec().getContainers() != null) {
                    for (V1Container c : pod.getSpec().getContainers()) {
                        images.add(c.getImage());
                    }
                }
                row.put("images", images);
                rows.add(row);
            }
            return rows;
        } catch (Exception ex) {
            throw new UpstreamException("K8S_ERROR", "list pods failed: " + ex.getMessage(), ex);
        }
    }

    public List<Map<String, Object>> listDeployments(String namespace) {
        requireClient();
        try {
            V1DeploymentList list;
            if (StringUtils.hasText(namespace) && !namespace.equals("*") && !namespace.equals("all")) {
                list = appsApi.listNamespacedDeployment(namespace).execute();
            } else {
                list = appsApi.listDeploymentForAllNamespaces().execute();
            }
            List<Map<String, Object>> rows = new ArrayList<>();
            if (list.getItems() == null) {
                return rows;
            }
            for (V1Deployment d : list.getItems()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", d.getMetadata() != null ? d.getMetadata().getName() : null);
                row.put("namespace", d.getMetadata() != null ? d.getMetadata().getNamespace() : null);
                row.put("replicas", d.getSpec() != null ? d.getSpec().getReplicas() : null);
                row.put("readyReplicas", d.getStatus() != null ? d.getStatus().getReadyReplicas() : 0);
                row.put("availableReplicas", d.getStatus() != null ? d.getStatus().getAvailableReplicas() : 0);
                List<String> images = new ArrayList<>();
                if (d.getSpec() != null && d.getSpec().getTemplate() != null
                        && d.getSpec().getTemplate().getSpec() != null
                        && d.getSpec().getTemplate().getSpec().getContainers() != null) {
                    for (V1Container c : d.getSpec().getTemplate().getSpec().getContainers()) {
                        images.add(c.getImage());
                    }
                }
                row.put("images", images);
                rows.add(row);
            }
            return rows;
        } catch (Exception ex) {
            throw new UpstreamException("K8S_ERROR", "list deployments failed: " + ex.getMessage(), ex);
        }
    }

    public void deletePod(String namespace, String name) {
        requireClient();
        try {
            coreApi.deleteNamespacedPod(name, namespace).execute();
        } catch (Exception ex) {
            throw new UpstreamException("K8S_ERROR", "delete pod failed: " + ex.getMessage(), ex);
        }
    }

    public void restartDeployment(String namespace, String name) {
        requireClient();
        try {
            // patch annotation to trigger rollout
            V1Deployment dep = appsApi.readNamespacedDeployment(name, namespace).execute();
            if (dep.getSpec() == null || dep.getSpec().getTemplate() == null
                    || dep.getSpec().getTemplate().getMetadata() == null) {
                throw new UpstreamException("K8S_ERROR", "invalid deployment object");
            }
            Map<String, String> annotations = dep.getSpec().getTemplate().getMetadata().getAnnotations();
            if (annotations == null) {
                annotations = new LinkedHashMap<>();
                dep.getSpec().getTemplate().getMetadata().setAnnotations(annotations);
            }
            annotations.put("cloudlab.io/restartedAt", java.time.Instant.now().toString());
            appsApi.replaceNamespacedDeployment(name, namespace, dep).execute();
        } catch (UpstreamException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UpstreamException("K8S_ERROR", "restart deployment failed: " + ex.getMessage(), ex);
        }
    }

    private void requireClient() {
        if (!isAvailable()) {
            throw new UpstreamException("K8S_UNAVAILABLE", "Kubernetes API is not reachable (set kubeconfig / run in-cluster)");
        }
    }
}
