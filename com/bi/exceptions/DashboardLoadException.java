package com.bi.exceptions;

public class DashboardLoadException extends RuntimeException {
    public DashboardLoadException() {
        super();
    }

    public DashboardLoadException(String message) {
        super(message);
    }

    public DashboardLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
