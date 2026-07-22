package com.cloudlab.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cloudlab.common.UpstreamException;
import com.cloudlab.config.CloudLabProperties;

@Component
public class GitHubActionsAdapter {

    private final CloudLabProperties properties;
    private final RestClient client;

    public GitHubActionsAdapter(RestClient.Builder builder, CloudLabProperties properties) {
        this.properties = properties;
        this.client = builder.baseUrl("https://api.github.com").build();
    }

    public boolean isConfigured() {
        var i = properties.getIntegrations();
        return StringUtils.hasText(i.getGithubToken())
                && StringUtils.hasText(i.getGithubOwner())
                && StringUtils.hasText(i.getGithubRepo());
    }

    public List<Map<String, Object>> listWorkflowRuns(int perPage) {
        if (!isConfigured()) {
            return Collections.emptyList();
        }
        var i = properties.getIntegrations();
        try {
            Map<String, Object> body = client.get()
                    .uri("/repos/{owner}/{repo}/actions/runs?per_page={perPage}",
                            i.getGithubOwner(), i.getGithubRepo(), perPage)
                    .header("Authorization", "Bearer " + i.getGithubToken())
                    .header("Accept", "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (body == null || body.get("workflow_runs") == null) {
                return Collections.emptyList();
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> runs = (List<Map<String, Object>>) body.get("workflow_runs");
            return runs;
        } catch (RestClientException ex) {
            throw new UpstreamException("GITHUB_UNAVAILABLE", "GitHub Actions API failed: " + ex.getMessage(), ex);
        }
    }
}
