package com.cloudlab.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;

@Component
public class AlertmanagerAdapter {

    private final RestClient client;

    public AlertmanagerAdapter(RestClient.Builder builder, CloudLabProperties properties) {
        this.client = builder.baseUrl(properties.getIntegrations().getAlertmanagerUrl()).build();
    }

    public List<Map<String, Object>> alerts() {
        try {
            List<Map<String, Object>> body = client.get()
                    .uri("/api/v2/alerts")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            return body != null ? body : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new UpstreamException("ALERTMANAGER_UNAVAILABLE", "Alertmanager failed: " + ex.getMessage(), ex);
        }
    }

    public boolean ready() {
        try {
            String body = client.get().uri("/-/ready").retrieve().body(String.class);
            return body != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
