package com.bayzdelivery.controller;

import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.service.DeliveryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

  @Autowired
  private DeliveryService deliveryService;

  @PostMapping
  public ResponseEntity<Delivery> createNewDelivery(@RequestBody Delivery delivery) {
    return ResponseEntity.ok(deliveryService.save(delivery));
  }

  @GetMapping("/{deliveryId}")
  public ResponseEntity<Delivery> getDeliveryById(@PathVariable("deliveryId") Long deliveryId) {
    Delivery delivery = deliveryService.findById(deliveryId);
    if (delivery != null) {
      return ResponseEntity.ok(delivery);
    }
    return ResponseEntity.notFound().build();
  }
}
