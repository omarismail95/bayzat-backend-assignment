package com.bayzdelivery.dto;

import java.math.BigDecimal;

/**
 * Response DTO representing a top-performing delivery man entry
 * in the commission leaderboard.
 *
 * @author Omar Ismail
 */
public class TopDeliveryManResponse {

    private Long deliveryManId;
    private String deliveryManName;
    private BigDecimal totalCommission;
    private BigDecimal averageCommission;

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

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public BigDecimal getAverageCommission() {
        return averageCommission;
    }

    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }
}
