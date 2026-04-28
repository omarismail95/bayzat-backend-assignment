package com.bayzdelivery.controller;

import com.bayzdelivery.dto.PersonRequest;
import com.bayzdelivery.dto.PersonResponse;
import com.bayzdelivery.model.PersonRole;
import com.bayzdelivery.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @Test
    void register_shouldReturnCreatedPerson() throws Exception {
        PersonRequest request = new PersonRequest();
        request.setName("Omar Ismail");
        request.setEmail("omar@example.com");
        request.setRegistrationNumber("REG-001");
        request.setRole(PersonRole.DELIVERY_MAN);

        PersonResponse response = new PersonResponse();
        response.setId(1L);
        response.setName("Omar Ismail");
        response.setEmail("omar@example.com");
        response.setRegistrationNumber("REG-001");
        response.setRole(PersonRole.DELIVERY_MAN);

        when(personService.register(any(PersonRequest.class))).thenReturn(response);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Omar Ismail"))
                .andExpect(jsonPath("$.email").value("omar@example.com"))
                .andExpect(jsonPath("$.registrationNumber").value("REG-001"))
                .andExpect(jsonPath("$.role").value("DELIVERY_MAN"));

        verify(personService).register(any(PersonRequest.class));
    }

    @Test
    void getAllPersons_shouldReturnPersonsList() throws Exception {
        PersonResponse first = new PersonResponse();
        first.setId(1L);
        first.setName("Omar Ismail");
        first.setEmail("omar@example.com");
        first.setRegistrationNumber("REG-001");
        first.setRole(PersonRole.DELIVERY_MAN);

        PersonResponse second = new PersonResponse();
        second.setId(2L);
        second.setName("Ali Ahmad");
        second.setEmail("ali@example.com");
        second.setRegistrationNumber("REG-002");
        second.setRole(PersonRole.CUSTOMER);

        when(personService.getAll()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Omar Ismail"))
                .andExpect(jsonPath("$[0].email").value("omar@example.com"))
                .andExpect(jsonPath("$[0].role").value("DELIVERY_MAN"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Ali Ahmad"))
                .andExpect(jsonPath("$[1].email").value("ali@example.com"))
                .andExpect(jsonPath("$[1].role").value("CUSTOMER"));

        verify(personService).getAll();
    }

    @Test
    void getPersonById_shouldReturnPerson() throws Exception {
        PersonResponse response = new PersonResponse();
        response.setId(1L);
        response.setName("Omar Ismail");
        response.setEmail("omar@example.com");
        response.setRegistrationNumber("REG-001");
        response.setRole(PersonRole.DELIVERY_MAN);

        when(personService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/persons/{personId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Omar Ismail"))
                .andExpect(jsonPath("$.email").value("omar@example.com"))
                .andExpect(jsonPath("$.registrationNumber").value("REG-001"))
                .andExpect(jsonPath("$.role").value("DELIVERY_MAN"));

        verify(personService).findById(1L);
    }

    @Test
    void register_whenRequestInvalid_shouldReturnBadRequest() throws Exception {
        PersonRequest request = new PersonRequest();
        request.setName("");
        request.setEmail("invalid-email");
        request.setRole(null);

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
