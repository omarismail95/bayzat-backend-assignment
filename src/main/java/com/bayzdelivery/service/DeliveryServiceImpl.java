package com.bayzdelivery.service;

import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.repositories.DeliveryRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public Delivery save(Delivery delivery) {

        Long deliveryManId = delivery.getDeliveryMan().getId();

        List<Delivery> overlappingDeliveries =
                deliveryRepository.findByDeliveryManIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        deliveryManId,
                        delivery.getEndTime(),
                        delivery.getStartTime()
                );

        BigDecimal commission = getBigDecimal(delivery, overlappingDeliveries);

        delivery.setCommission(commission);

        return deliveryRepository.save(delivery);
    }

    @NonNull
    private static BigDecimal getBigDecimal(Delivery delivery, List<Delivery> overlappingDeliveries) {
        if (!overlappingDeliveries.isEmpty()) {
            throw new IllegalStateException(
                    "Delivery man already has an active delivery during this time window"
            );
        }

        BigDecimal price = delivery.getPrice() != null
                ? delivery.getPrice()
                : BigDecimal.ZERO;

        BigDecimal distance = delivery.getDistance() != null
                ? delivery.getDistance()
                : BigDecimal.ZERO;

        return price
                .multiply(new BigDecimal("0.05"))
                .add(distance.multiply(new BigDecimal("0.5")));
    }

    @Override
    public Delivery findById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery not found with id: " + deliveryId)
                );
    }
}
