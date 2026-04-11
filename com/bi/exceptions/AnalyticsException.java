package com.bi.exceptions;

public class AnalyticsException extends RuntimeException {
    public AnalyticsException() {
        super();
    }

    public AnalyticsException(String message) {
        super(message);
    }

    public AnalyticsException(String message, Throwable cause) {
        super(message, cause);
    }
}
