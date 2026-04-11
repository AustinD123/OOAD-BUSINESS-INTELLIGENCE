package com.bi.exceptions;

public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException() {
        super();
    }

    public ReportGenerationException(String message) {
        super(message);
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
