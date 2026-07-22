package com.cloudlab.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudlab.service.PlatformOverviewService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ServerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlatformOverviewService overviewService;

    @Test
    void serverStatusReturnsEnvelope() throws Exception {
        when(overviewService.serverStatus()).thenReturn(Map.of(
                "prometheus", true,
                "cpuPercent", 10.0
        ));

        mockMvc.perform(get("/api/server/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.prometheus").value(true))
                .andExpect(jsonPath("$.data.cpuPercent").value(10.0));
    }
}
