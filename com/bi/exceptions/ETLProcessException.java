package com.bi.exceptions;

public class ETLProcessException extends RuntimeException {
    public ETLProcessException() {
        super();
    }

    public ETLProcessException(String message) {
        super(message);
    }

    public ETLProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
