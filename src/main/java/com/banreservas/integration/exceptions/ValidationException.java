package com.banreservas.integration.exceptions;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}