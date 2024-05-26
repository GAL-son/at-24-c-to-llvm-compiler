package com.at24.exceptions;

public class SyntaxException extends RuntimeException{
    public SyntaxException(String message) {
        super(buildSyntaxMessage(message));
    }

    private static String buildSyntaxMessage(String message) {
        return "Syntax error: " + message;
    }
}
