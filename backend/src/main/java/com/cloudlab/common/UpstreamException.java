package com.cloudlab.common;

public class UpstreamException extends RuntimeException {

    private final String code;

    public UpstreamException(String code, String message) {
        super(message);
        this.code = code;
    }

    public UpstreamException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
