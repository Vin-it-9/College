package org.nexus.exception;

public class LabNotFoundException extends RuntimeException {
    public LabNotFoundException(String message) {
        super(message);
    }
}
