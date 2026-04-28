package com.bayzdelivery.exceptions;

/**
 * Thrown when a request violates a business rule,
 * such as assigning a customer role to a delivery slot.
 *
 * @author Omar Ismail
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
