package com.cloudlab.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudlab.adapter.PrometheusAdapter;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class PrometheusControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PrometheusAdapter prometheusAdapter;

    @Test
    void summaryOk() throws Exception {
        when(prometheusAdapter.scalarOrZero(anyString())).thenReturn(1.5);
        when(prometheusAdapter.ready()).thenReturn(true);

        mockMvc.perform(get("/api/prometheus/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ready").value(true))
                .andExpect(jsonPath("$.data.cpu").value(1.5));
    }
}
