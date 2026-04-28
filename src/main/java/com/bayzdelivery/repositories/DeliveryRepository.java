package com.bayzdelivery.repositories;

import com.bayzdelivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Delivery entity persistence operations.
 * Contains custom JPQL queries for business logic enforcement
 * and commission reporting.
 *
 * @author Omar Ismail
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Checks whether a delivery man has any active delivery that overlaps
     * with the proposed time window. Used to enforce the business rule
     * that a delivery man cannot handle multiple deliveries simultaneously.
     *
     * @param deliveryManId the ID of the delivery man to check
     * @param startTime     the proposed delivery start time
     * @param endTime       the proposed delivery end time
     * @return true if an overlapping delivery exists
     */
    @Query("""
                SELECT COUNT(d) > 0 FROM Delivery d
                WHERE d.deliveryMan.id = :deliveryManId
                AND d.startTime < :endTime
                AND d.endTime > :startTime
            """)
    boolean existsOverlappingDelivery(
            @Param("deliveryManId") Long deliveryManId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Finds deliveries that started before the given threshold time
     * and have not yet ended. Used by the scheduled job to detect
     * deliveries running longer than 45 minutes.
     *
     * @param threshold the cutoff time (now minus 45 minutes)
     * @return list of deliveries that are overdue
     */
    @Query("""
                SELECT d FROM Delivery d
                JOIN FETCH d.deliveryMan dm
                JOIN FETCH d.customer c
                WHERE d.endTime IS NULL
                AND d.startTime < :threshold
            """)
    List<Delivery> findDelayedDeliveries(@Param("threshold") LocalDateTime threshold);

    /**
     * Returns the top delivery men by total commission earned
     * within a given time range, along with their average commission.
     * Uses JOIN FETCH to avoid N+1 when accessing deliveryMan fields.
     *
     * @param startTime start of the reporting period
     * @param endTime   end of the reporting period
     * @return list of Object arrays: [deliveryManId, name, totalCommission, averageCommission]
     */
    @Query("""
                SELECT d.deliveryMan.id,
                       d.deliveryMan.name,
                       SUM(d.commission),
                       AVG(d.commission)
                FROM Delivery d
                WHERE d.startTime >= :startTime
                AND d.startTime <= :endTime
                AND d.commission IS NOT NULL
                GROUP BY d.deliveryMan.id, d.deliveryMan.name
                ORDER BY SUM(d.commission) DESC
                LIMIT 3
            """)
    List<Object[]> findTopDeliveryMenByCommission(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Delivery> findByDeliveryManIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long deliveryManId, LocalDateTime endTime, LocalDateTime startTime);

}
