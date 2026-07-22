package com.cloudlab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudlab")
public class CloudLabProperties {

    private String version = "0.1.0-SNAPSHOT";
    private Cors cors = new Cors();
    private Integrations integrations = new Integrations();
    private Security security = new Security();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Integrations getIntegrations() {
        return integrations;
    }

    public void setIntegrations(Integrations integrations) {
        this.integrations = integrations;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:3000";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class Security {
        /** When true, all /api/** are public (lab/demo until full JWT). */
        private boolean openApi = true;

        public boolean isOpenApi() {
            return openApi;
        }

        public void setOpenApi(boolean openApi) {
            this.openApi = openApi;
        }
    }

    public static class Integrations {
        private String prometheusUrl = "http://localhost:9090";
        private String lokiUrl = "http://localhost:3100";
        private String alertmanagerUrl = "http://localhost:9093";
        private String dockerHost = "unix:///var/run/docker.sock";
        private String kubeconfig = "";
        private String githubToken = "";
        private String githubOwner = "";
        private String githubRepo = "";

        public String getPrometheusUrl() {
            return prometheusUrl;
        }

        public void setPrometheusUrl(String prometheusUrl) {
            this.prometheusUrl = prometheusUrl;
        }

        public String getLokiUrl() {
            return lokiUrl;
        }

        public void setLokiUrl(String lokiUrl) {
            this.lokiUrl = lokiUrl;
        }

        public String getAlertmanagerUrl() {
            return alertmanagerUrl;
        }

        public void setAlertmanagerUrl(String alertmanagerUrl) {
            this.alertmanagerUrl = alertmanagerUrl;
        }

        public String getDockerHost() {
            return dockerHost;
        }

        public void setDockerHost(String dockerHost) {
            this.dockerHost = dockerHost;
        }

        public String getKubeconfig() {
            return kubeconfig;
        }

        public void setKubeconfig(String kubeconfig) {
            this.kubeconfig = kubeconfig;
        }

        public String getGithubToken() {
            return githubToken;
        }

        public void setGithubToken(String githubToken) {
            this.githubToken = githubToken;
        }

        public String getGithubOwner() {
            return githubOwner;
        }

        public void setGithubOwner(String githubOwner) {
            this.githubOwner = githubOwner;
        }

        public String getGithubRepo() {
            return githubRepo;
        }

        public void setGithubRepo(String githubRepo) {
            this.githubRepo = githubRepo;
        }
    }
}
