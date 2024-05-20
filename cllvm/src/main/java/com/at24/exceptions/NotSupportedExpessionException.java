package com.at24.exceptions;

public class NotSupportedExpessionException extends RuntimeException {
    public NotSupportedExpessionException(String message) {
        super("Expresion not supported: " + message);
    }
}
