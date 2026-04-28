package com.bayzdelivery.service;

import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Person;
import com.bayzdelivery.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> getAll() {
        List<Person> personList = new ArrayList<>();
        personRepository.findAll().forEach(personList::add);
        return personList;
    }

    @Override
    public Person save(Person p) {
        return personRepository.save(p);
    }

    @Override
    public Person findById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Person not found with id: " + personId)
                );
    }
}
