package com.bayzdelivery.controller;

import com.bayzdelivery.dto.DeliveryRequest;
import com.bayzdelivery.dto.DeliveryResponse;
import com.bayzdelivery.dto.TopDeliveryManResponse;
import com.bayzdelivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing delivery operations and commission reporting.
 *
 * @author Omar Ismail
 */
@RestController
@RequestMapping("/deliveries")
@Tag(name = "Deliveries", description = "Delivery creation, retrieval, and commission reporting")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Create a new delivery",
            description = "Records a completed delivery. Commission is calculated automatically.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Delivery man or customer not found"),
            @ApiResponse(responseCode = "409", description = "Delivery man already has active delivery")
    })
    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody DeliveryRequest request
    ) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get delivery by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery found"),
            @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @Parameter(description = "ID of the delivery to retrieve")
            @PathVariable Long deliveryId
    ) {
        return ResponseEntity.ok(deliveryService.findById(deliveryId));
    }

    @Operation(
            summary = "Get top 3 delivery men by commission",
            description = "Returns the top 3 delivery men ranked by total commission earned "
                    + "within the specified time interval, along with average commission."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top delivery men returned"),
            @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    @GetMapping("/top-performers")
    public ResponseEntity<List<TopDeliveryManResponse>> getTopDeliveryMen(
            @Parameter(description = "Start of the time interval (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End of the time interval (ISO format: yyyy-MM-ddTHH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        return ResponseEntity.ok(deliveryService.getTopDeliveryMen(startTime, endTime));
    }
}
