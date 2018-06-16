package com.bullhorn.exceptions;

public class InboundJSONException extends Exception{
    private String message;

    public InboundJSONException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String toString() {
        return "InboundJSONException - "+message;
    }
}
