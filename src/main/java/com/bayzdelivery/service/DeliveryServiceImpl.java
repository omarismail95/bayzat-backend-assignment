package com.bayzdelivery.service;

import com.bayzdelivery.dto.DeliveryRequest;
import com.bayzdelivery.dto.DeliveryResponse;
import com.bayzdelivery.dto.TopDeliveryManResponse;
import com.bayzdelivery.exceptions.BusinessRuleException;
import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.model.Person;
import com.bayzdelivery.model.PersonRole;
import com.bayzdelivery.repositories.DeliveryRepository;
import com.bayzdelivery.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link DeliveryService} providing delivery management
 * and commission calculation operations.
 *
 * @author Omar Ismail
 */
@Service
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    private static final BigDecimal COMMISSION_PRICE_RATE = new BigDecimal("0.05");
    private static final BigDecimal COMMISSION_DISTANCE_RATE = new BigDecimal("0.5");

    private final DeliveryRepository deliveryRepository;
    private final PersonRepository personRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository,
                               PersonRepository personRepository) {
        this.deliveryRepository = deliveryRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        log.info("Creating delivery for deliveryManId: {}, customerId: {}",
                request.getDeliveryManId(), request.getCustomerId());

        Person deliveryMan = personRepository.findById(request.getDeliveryManId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery man not found with id: " + request.getDeliveryManId()));

        if (deliveryMan.getRole() != PersonRole.DELIVERY_MAN) {
            throw new BusinessRuleException("Person with id "
                    + request.getDeliveryManId() + " is not registered as a delivery man");
        }

        Person customer = personRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        if (customer.getRole() != PersonRole.CUSTOMER) {
            throw new BusinessRuleException("Person with id "
                    + request.getCustomerId() + " is not registered as a customer");
        }

        boolean hasOverlap = deliveryRepository.existsOverlappingDelivery(
                request.getDeliveryManId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasOverlap) {
            throw new BusinessRuleException("Delivery man already has an active delivery "
                    + "during the requested time window");
        }

        BigDecimal commission = calculateCommission(request.getPrice(), request.getDistance());

        Delivery delivery = new Delivery();
        delivery.setDeliveryMan(deliveryMan);
        delivery.setCustomer(customer);
        delivery.setStartTime(request.getStartTime());
        delivery.setEndTime(request.getEndTime());
        delivery.setDistance(request.getDistance());
        delivery.setPrice(request.getPrice());
        delivery.setCommission(commission);

        Delivery saved = deliveryRepository.save(delivery);
        log.info("Delivery created with id: {}, commission: {}", saved.getId(), commission);
        return DeliveryResponse.from(saved);
    }

    @Override
    public DeliveryResponse findById(Long id) {
        log.debug("Fetching delivery with id: {}", id);
        return deliveryRepository.findById(id)
                .map(DeliveryResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
    }

    @Override
    public List<TopDeliveryManResponse> getTopDeliveryMen(LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        log.info("Fetching top 3 delivery men from {} to {}", startTime, endTime);

        return deliveryRepository.findTopDeliveryMenByCommission(startTime, endTime)
                .stream()
                .map(row -> {
                    TopDeliveryManResponse dto = new TopDeliveryManResponse();
                    dto.setDeliveryManId((Long) row[0]);
                    dto.setDeliveryManName((String) row[1]);
                    dto.setTotalCommission(((BigDecimal) row[2]).setScale(2, RoundingMode.HALF_UP));
                    dto.setAverageCommission(((BigDecimal) row[3]).setScale(2, RoundingMode.HALF_UP));
                    return dto;
                })
                .toList();
    }

    /**
     * Calculates delivery commission using the formula:
     * Commission = price * 0.05 + distance * 0.5
     *
     * @param price    the order price
     * @param distance the delivery distance in km
     * @return calculated commission rounded to 2 decimal places
     */
    private BigDecimal calculateCommission(BigDecimal price, BigDecimal distance) {
        return price.multiply(COMMISSION_PRICE_RATE)
                .add(distance.multiply(COMMISSION_DISTANCE_RATE))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
