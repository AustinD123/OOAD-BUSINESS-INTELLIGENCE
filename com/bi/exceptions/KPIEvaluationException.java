package com.bi.exceptions;

public class KPIEvaluationException extends RuntimeException {
    public KPIEvaluationException() {
        super();
    }

    public KPIEvaluationException(String message) {
        super(message);
    }

    public KPIEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
