package com.baduwal.ecommerce.data.entity;

import com.baduwal.ecommerce.data.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private BigDecimal amount;

    private String provider; //Stripe,Paypal

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String transactionId; // provide transaction id

    private LocalDateTime createdAt = LocalDateTime.now();


}
