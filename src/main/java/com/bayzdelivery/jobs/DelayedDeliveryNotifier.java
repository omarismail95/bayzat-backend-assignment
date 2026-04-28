package com.bayzdelivery.jobs;

import com.bayzdelivery.model.Delivery;
import com.bayzdelivery.repositories.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job that periodically checks for deliveries that have been
 * running for more than 45 minutes without being completed.
 * When found, the customer support team is notified asynchronously.
 *
 * @author Omar Ismail
 */
@Component
public class DelayedDeliveryNotifier {

    private static final Logger log = LoggerFactory.getLogger(DelayedDeliveryNotifier.class);

    private static final long DELAY_THRESHOLD_MINUTES = 45;

    private final DeliveryRepository deliveryRepository;

    public DelayedDeliveryNotifier(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Runs every 3 minutes to check for deliveries that started
     * more than 45 minutes ago and have not yet been completed.
     * For each delayed delivery found, notifies the CS team asynchronously.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.delay-check-ms:180000}")
    public void checkDelayedDeliveries() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(DELAY_THRESHOLD_MINUTES);
        log.debug("Checking for deliveries started before {}", threshold);

        List<Delivery> delayedDeliveries = deliveryRepository.findDelayedDeliveries(threshold);

        if (delayedDeliveries.isEmpty()) {
            log.debug("No delayed deliveries found");
            return;
        }

        log.warn("Found {} delayed delivery/deliveries exceeding {} minutes",
                delayedDeliveries.size(), DELAY_THRESHOLD_MINUTES);

        delayedDeliveries.forEach(this::notifyCustomerSupport);
    }

    /**
     * Notifies the customer support team about a specific delayed delivery.
     * This method runs in a separate thread to avoid blocking the scheduler.
     *
     * @param delivery the delayed delivery to report
     */
    @Async("taskExecutor")
    public void notifyCustomerSupport(Delivery delivery) {
        log.warn(
                "ALERT [CS Team]: Delivery ID {} has been running since {} (DeliveryMan: {}, Customer: {}) "
                        + "and has exceeded the {} minute threshold.",
                delivery.getId(),
                delivery.getStartTime(),
                delivery.getDeliveryMan().getName(),
                delivery.getCustomer().getName(),
                DELAY_THRESHOLD_MINUTES
        );
        // TODO: Replace with actual notification (email, Slack, PagerDuty, etc.)
    }
}
