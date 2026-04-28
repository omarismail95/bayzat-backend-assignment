package com.bayzdelivery.dto;

import com.bayzdelivery.model.PersonRole;

/**
 * Response DTO for person data returned from the API.
 *
 * @author Omar Ismail
 */
public class PersonResponse {

    private Long id;
    private String name;
    private String email;
    private String registrationNumber;
    private PersonRole role;

    // Constructor from entity:
    public static PersonResponse from(com.bayzdelivery.model.Person person) {
        PersonResponse response = new PersonResponse();
        response.id = person.getId();
        response.name = person.getName();
        response.email = person.getEmail();
        response.registrationNumber = person.getRegistrationNumber();
        response.role = person.getRole();
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public PersonRole getRole() {
        return role;
    }

    public void setRole(PersonRole role) {
        this.role = role;
    }
}
