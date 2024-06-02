package com.at24.exceptions;

public class SemanticException extends RuntimeException {
    public SemanticException(String msg) {
        super("Semantic Exception: " + msg);
    }
}
