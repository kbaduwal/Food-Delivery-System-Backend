package com.baduwal.ecommerce.data.entity;

import com.baduwal.ecommerce.data.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long courierId; // optional reference to courier / rider

    private OrderStatus status = OrderStatus.ORDER_PLACED;

    // simple lat/lon, update as tracking progress
    private Double currentLatitude;
    private Double currentLongitude;

    private LocalDateTime estimatedArrivalTime;

    private LocalDateTime createdAt = LocalDateTime.now();
}
