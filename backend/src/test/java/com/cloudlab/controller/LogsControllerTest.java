package com.cloudlab.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.cloudlab.adapter.LokiAdapter;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class LogsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    LokiAdapter lokiAdapter;

    @Test
    void queryLogsOk() throws Exception {
        when(lokiAdapter.queryRange(anyString(), anyLong(), anyLong(), anyInt()))
                .thenReturn(Map.of("status", "success", "data", Map.of("result", java.util.List.of())));

        mockMvc.perform(get("/api/logs")
                        .param("query", "{compose_service=\"backend\"}")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("success"));
    }
}
