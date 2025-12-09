package com.baduwal.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void notifyUserOrderConfirmed(Long userId, Long orderId, String message) {
        // stub: log + TODO hook for FCM/email
        log.info("Notify user {} about order {}: {}", userId, orderId, message);

        // Example: If using Firebase Cloud Messaging, call FCM SDK here to send push
        // Example: If using email, call an EmailService here

    }
}
