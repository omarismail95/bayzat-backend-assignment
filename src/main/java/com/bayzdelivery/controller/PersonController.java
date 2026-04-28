package com.bayzdelivery.controller;

import com.bayzdelivery.dto.PersonRequest;
import com.bayzdelivery.dto.PersonResponse;
import com.bayzdelivery.exceptions.ErrorResponse;
import com.bayzdelivery.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing person registration and retrieval.
 * Persons can be registered as either customers or delivery men.
 *
 * @author Omar Ismail
 */
@RestController
@RequestMapping("/persons")
@Tag(name = "Persons", description = "Person registration and management")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(summary = "Register a new person",
            description = "Registers a person as either CUSTOMER or DELIVERY_MAN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PersonResponse> register(@Valid @RequestBody PersonRequest request) {
        PersonResponse response = personService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all persons", description = "Returns all registered persons")
    @ApiResponse(responseCode = "200", description = "List returned successfully")
    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAllPersons() {
        return ResponseEntity.ok(personService.getAll());
    }

    @Operation(summary = "Get person by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @GetMapping("/{personId}")
    public ResponseEntity<PersonResponse> getPersonById(
            @Parameter(description = "ID of the person to retrieve")
            @PathVariable Long personId
    ) {
        return ResponseEntity.ok(personService.findById(personId));
    }
}
