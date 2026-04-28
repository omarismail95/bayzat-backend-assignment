package com.bayzdelivery.service;

import com.bayzdelivery.dto.PersonRequest;
import com.bayzdelivery.dto.PersonResponse;

import java.util.List;

/**
 * Service interface defining operations for person management.
 *
 * @author Omar Ismail
 */
public interface PersonService {

    /**
     * Registers a new person in the system.
     * A person must choose exactly one role: CUSTOMER or DELIVERY_MAN.
     *
     * @param request the registration request data
     * @return the created person as a response DTO
     */
    PersonResponse register(PersonRequest request);

    /**
     * Retrieves all persons registered in the system.
     *
     * @return list of all persons
     */
    List<PersonResponse> getAll();

    /**
     * Finds a person by their unique identifier.
     *
     * @param id the person's ID
     * @return the found person as a response DTO
     * @throws com.bayzdelivery.exceptions.ResourceNotFoundException if not found
     */
    PersonResponse findById(Long id);
}
