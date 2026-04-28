package com.bayzdelivery.service;

import com.bayzdelivery.dto.PersonRequest;
import com.bayzdelivery.dto.PersonResponse;
import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Person;
import com.bayzdelivery.model.PersonRole;
import com.bayzdelivery.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonServiceImpl personService;

    @Test
    void register_shouldCreatePersonSuccessfully() {
        PersonRequest request = new PersonRequest();
        request.setName("Omar Ismail");
        request.setEmail("omar@example.com");
        request.setRegistrationNumber("REG-001");
        request.setRole(PersonRole.DELIVERY_MAN);

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setName("Omar Ismail");
        savedPerson.setEmail("omar@example.com");
        savedPerson.setRegistrationNumber("REG-001");
        savedPerson.setRole(PersonRole.DELIVERY_MAN);

        when(personRepository.existsByEmail("omar@example.com")).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

        PersonResponse response = personService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Omar Ismail", response.getName());
        assertEquals("omar@example.com", response.getEmail());
        assertEquals("REG-001", response.getRegistrationNumber());
        assertEquals(PersonRole.DELIVERY_MAN, response.getRole());

        verify(personRepository).existsByEmail("omar@example.com");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrowIllegalArgumentException() {
        PersonRequest request = new PersonRequest();
        request.setName("Omar Ismail");
        request.setEmail("omar@example.com");
        request.setRegistrationNumber("REG-001");
        request.setRole(PersonRole.CUSTOMER);

        when(personRepository.existsByEmail("omar@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> personService.register(request)
        );

        assertEquals("Email already registered: omar@example.com", exception.getMessage());

        verify(personRepository).existsByEmail("omar@example.com");
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void getAll_shouldReturnAllPersons() {
        Person first = new Person();
        first.setId(1L);
        first.setName("Omar Ismail");
        first.setEmail("omar@example.com");
        first.setRegistrationNumber("REG-001");
        first.setRole(PersonRole.DELIVERY_MAN);

        Person second = new Person();
        second.setId(2L);
        second.setName("Ali Ahmad");
        second.setEmail("ali@example.com");
        second.setRegistrationNumber("REG-002");
        second.setRole(PersonRole.CUSTOMER);

        when(personRepository.findAll()).thenReturn(List.of(first, second));

        List<PersonResponse> responses = personService.getAll();

        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Omar Ismail", responses.get(0).getName());
        assertEquals("omar@example.com", responses.get(0).getEmail());
        assertEquals("REG-001", responses.get(0).getRegistrationNumber());
        assertEquals(PersonRole.DELIVERY_MAN, responses.get(0).getRole());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Ali Ahmad", responses.get(1).getName());
        assertEquals("ali@example.com", responses.get(1).getEmail());
        assertEquals("REG-002", responses.get(1).getRegistrationNumber());
        assertEquals(PersonRole.CUSTOMER, responses.get(1).getRole());

        verify(personRepository).findAll();
    }

    @Test
    void getAll_whenNoPersonsExist_shouldReturnEmptyList() {
        when(personRepository.findAll()).thenReturn(List.of());

        List<PersonResponse> responses = personService.getAll();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(personRepository).findAll();
    }

    @Test
    void findById_whenPersonExists_shouldReturnPersonResponse() {
        Person person = new Person();
        person.setId(1L);
        person.setName("Omar Ismail");
        person.setEmail("omar@example.com");
        person.setRegistrationNumber("REG-001");
        person.setRole(PersonRole.DELIVERY_MAN);

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        PersonResponse response = personService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Omar Ismail", response.getName());
        assertEquals("omar@example.com", response.getEmail());
        assertEquals("REG-001", response.getRegistrationNumber());
        assertEquals(PersonRole.DELIVERY_MAN, response.getRole());

        verify(personRepository).findById(1L);
    }

    @Test
    void findById_whenPersonDoesNotExist_shouldThrowResourceNotFoundException() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> personService.findById(1L)
        );

        assertEquals("Person not found with id: 1", exception.getMessage());

        verify(personRepository).findById(1L);
    }
}
