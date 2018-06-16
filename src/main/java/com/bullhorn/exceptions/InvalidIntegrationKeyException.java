package com.bullhorn.exceptions;

public class InvalidIntegrationKeyException extends Exception{

    private String message;

    public InvalidIntegrationKeyException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "InvalidIntegrationKeyException - "+message;
    }
}
