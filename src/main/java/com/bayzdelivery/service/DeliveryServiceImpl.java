package com.bayzdelivery.service;

import java.util.Optional;

import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.repositories.DeliveryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImpl implements DeliveryService {

  @Autowired
  private DeliveryRepository deliveryRepository;

  @Override
  public Delivery save(Delivery delivery) {

    long price = delivery.getPrice() != null ? delivery.getPrice() : 0L;
    long distance = delivery.getDistance() != null ? delivery.getDistance() : 0L;

    // Commission = price * 0.05 + distance * 0.5
    Long commission = (long) (price * 0.05 + distance * 0.5);

    delivery.setComission(commission);

    return deliveryRepository.save(delivery);
  }

  @Override
  public Delivery findById(Long deliveryId) {
    Optional<Delivery> optionalDelivery = deliveryRepository.findById(deliveryId);
    return optionalDelivery.orElse(null);
  }
}
