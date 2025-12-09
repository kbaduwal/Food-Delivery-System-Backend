package com.baduwal.ecommerce.service;


/*
 * Notification service. Replace FCM / APNs / Email provider as needed.
 */
public interface NotificationService{
    void notifyUserOrderConfirmed(Long userId, Long orderId, String message);
}
