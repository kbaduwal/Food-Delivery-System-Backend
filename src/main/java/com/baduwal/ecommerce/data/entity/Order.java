package com.baduwal.ecommerce.data.entity;

import com.baduwal.ecommerce.data.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    private Long restaurantId; //If order belongs to the single restaurent

    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //PENDING, CONFIRMED, DELIVERED

    private LocalDateTime createdAt=LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    public Order(List<CartItem> cartItems, int orderId, Long id, OrderStatus orderStatus) {
    }
}
