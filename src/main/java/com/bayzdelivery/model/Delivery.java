package com.bayzdelivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "delivery")
public class Delivery implements Serializable {

    private static final long serialVersionUID = 123765351514001L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(name = "start_time")
    Instant startTime;

    @NotNull
    @Column(name = "end_time")
    Instant endTime;

    @Column(name = "distance", precision = 19, scale = 2)
    BigDecimal distance;

    @Column(name = "price", precision = 19, scale = 2)
    BigDecimal price;

    @Column(name = "commission", precision = 19, scale = 2)
    BigDecimal commission;

    @ManyToOne
    @JoinColumn(name = "delivery_man_id", referencedColumnName = "id")
    Person deliveryMan;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    Person customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
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

    public Person getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(Person deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Delivery [id=" + id
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", distance=" + distance
                + ", price=" + price
                + ", commission=" + commission
                + ", deliveryMan=" + deliveryMan
                + ", customer=" + customer
                + "]";
    }
}
