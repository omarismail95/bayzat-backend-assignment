package com.bayzdelivery.exceptions;

/**
 * Thrown when a requested resource cannot be found in the system.
 *
 * @author Omar Ismail
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
