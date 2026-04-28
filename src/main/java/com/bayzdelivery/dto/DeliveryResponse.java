package com.bayzdelivery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for delivery data returned from the API.
 *
 * @author Omar Ismail
 */
public class DeliveryResponse {

    private Long id;
    private Long deliveryManId;
    private String deliveryManName;
    private Long customerId;
    private String customerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal distance;
    private BigDecimal price;
    private BigDecimal commission;

    public static DeliveryResponse from(com.bayzdelivery.model.Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.id = delivery.getId();
        response.deliveryManId = delivery.getDeliveryMan().getId();
        response.deliveryManName = delivery.getDeliveryMan().getName();
        response.customerId = delivery.getCustomer().getId();
        response.customerName = delivery.getCustomer().getName();
        response.startTime = delivery.getStartTime();
        response.endTime = delivery.getEndTime();
        response.distance = delivery.getDistance();
        response.price = delivery.getPrice();
        response.commission = delivery.getCommission();
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeliveryManId() {
        return deliveryManId;
    }

    public void setDeliveryManId(Long deliveryManId) {
        this.deliveryManId = deliveryManId;
    }

    public String getDeliveryManName() {
        return deliveryManName;
    }

    public void setDeliveryManName(String deliveryManName) {
        this.deliveryManName = deliveryManName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
