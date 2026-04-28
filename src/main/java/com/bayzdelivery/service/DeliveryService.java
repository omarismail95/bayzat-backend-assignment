package com.bayzdelivery.service;

import com.bayzdelivery.dto.DeliveryRequest;
import com.bayzdelivery.dto.DeliveryResponse;
import com.bayzdelivery.dto.TopDeliveryManResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface defining operations for delivery management.
 *
 * @author Omar Ismail
 */
public interface DeliveryService {

    /**
     * Creates a new delivery record after validating business constraints.
     * Calculates commission automatically.
     *
     * @param request the delivery creation request
     * @return the created delivery as a response DTO
     */
    DeliveryResponse createDelivery(DeliveryRequest request);

    /**
     * Finds a delivery by its unique identifier.
     *
     * @param id the delivery ID
     * @return the found delivery as a response DTO
     * @throws com.bayzdelivery.exceptions.ResourceNotFoundException if not found
     */
    DeliveryResponse findById(Long id);

    /**
     * Returns the top 3 delivery men by total commission earned
     * within the specified time range, including their average commission.
     *
     * @param startTime start of the reporting period
     * @param endTime   end of the reporting period
     * @return list of up to 3 top delivery men with commission statistics
     */
    List<TopDeliveryManResponse> getTopDeliveryMen(LocalDateTime startTime, LocalDateTime endTime);
}
