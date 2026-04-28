package com.bayzdelivery.service;

import com.bayzdelivery.exceptions.ResourceNotFoundException;
import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.repositories.DeliveryRepository;
import org.springframework.stereotype.Service;

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

        if (!overlappingDeliveries.isEmpty()) {
            throw new IllegalStateException(
                    "Delivery man already has an active delivery during this time window"
            );
        }

        long price = delivery.getPrice() != null ? delivery.getPrice() : 0L;
        long distance = delivery.getDistance() != null ? delivery.getDistance() : 0L;

        Long commission = (long) (price * 0.05 + distance * 0.5);

        delivery.setComission(commission);

        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery findById(Long deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery not found with id: " + deliveryId)
                );
    }
}
