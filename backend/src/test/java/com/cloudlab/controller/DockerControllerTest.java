package com.cloudlab.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudlab.adapter.DockerAdapter;
import com.cloudlab.common.UpstreamException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class DockerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    DockerAdapter dockerAdapter;

    @Test
    void listContainersOk() throws Exception {
        when(dockerAdapter.listContainers(true)).thenReturn(List.of(
                Map.of("id", "abc", "name", "demo", "state", "running")
        ));

        mockMvc.perform(get("/api/docker/containers?all=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("demo"));
    }

    @Test
    void restartContainerOk() throws Exception {
        doNothing().when(dockerAdapter).restart(eq("abc"));

        mockMvc.perform(post("/api/docker/containers/abc/restart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("restarted"));
    }

    @Test
    void dockerUnavailableReturns502() throws Exception {
        when(dockerAdapter.listContainers(true))
                .thenThrow(new UpstreamException("DOCKER_UNAVAILABLE", "socket missing"));

        mockMvc.perform(get("/api/docker/containers"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DOCKER_UNAVAILABLE"));
    }
}
