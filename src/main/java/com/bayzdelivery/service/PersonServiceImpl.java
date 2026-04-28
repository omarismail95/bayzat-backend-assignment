package com.bayzdelivery.service;

import com.bayzdelivery.dto.PersonRequest;
import com.bayzdelivery.dto.PersonResponse;
import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Person;
import com.bayzdelivery.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link PersonService} providing person management operations.
 *
 * @author Omar Ismail
 */
@Service
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public PersonResponse register(PersonRequest request) {
        log.info("Registering new person with email: {}", request.getEmail());

        if (personRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Person person = new Person();
        person.setName(request.getName());
        person.setEmail(request.getEmail());
        person.setRegistrationNumber(request.getRegistrationNumber());
        person.setRole(request.getRole());

        Person saved = personRepository.save(person);
        log.info("Person registered successfully with id: {}", saved.getId());
        return PersonResponse.from(saved);
    }

    @Override
    public List<PersonResponse> getAll() {
        log.debug("Fetching all persons");
        return personRepository.findAll()
                .stream()
                .map(PersonResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public PersonResponse findById(Long id) {
        log.debug("Fetching person with id: {}", id);
        return personRepository.findById(id)
                .map(PersonResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }
}
