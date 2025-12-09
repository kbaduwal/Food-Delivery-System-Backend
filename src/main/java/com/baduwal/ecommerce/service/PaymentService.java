package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.Payment;
import com.baduwal.ecommerce.data.enums.PaymentStatus;
import com.baduwal.ecommerce.repo.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /*
     * Simulate payment processing.
     * Replace with Stripe/Razorpay/PayPal integration.
     */

    public Payment processpayment(Long orderId, BigDecimal amount, String provider) {

        //Create payment record
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .provider(provider)
                .status(PaymentStatus.INITIATED)
                .build();

        payment = paymentRepository.save(payment);

        // Simulate calling provider and getting a result
        boolean success = simulateExternalPayment(provider, amount);

        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setTransactionId(UUID.randomUUID().toString());
        return paymentRepository.save(payment);
    }

    public Payment getOrderId(Long orderId) {
        return paymentRepository.findById(orderId).orElse(null);
    }

    private boolean simulateExternalPayment(String provider, BigDecimal amount) {
        // implement provider SDK here. For now: succeed for small amounts
        return amount.compareTo(BigDecimal.valueOf(1000))<0;
    }

}
