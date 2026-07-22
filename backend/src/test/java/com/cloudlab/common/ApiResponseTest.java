package com.cloudlab.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void okEnvelope() {
        ApiResponse<String> res = ApiResponse.ok("hello");
        assertThat(res.success()).isTrue();
        assertThat(res.data()).isEqualTo("hello");
        assertThat(res.error()).isNull();
    }

    @Test
    void failEnvelope() {
        ApiResponse<Void> res = ApiResponse.fail("CODE", "msg");
        assertThat(res.success()).isFalse();
        assertThat(res.data()).isNull();
        assertThat(res.error()).isNotNull();
        assertThat(res.error().code()).isEqualTo("CODE");
        assertThat(res.error().message()).isEqualTo("msg");
    }
}
