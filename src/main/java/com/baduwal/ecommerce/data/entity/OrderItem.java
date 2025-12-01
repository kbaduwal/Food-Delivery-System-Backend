package com.baduwal.ecommerce.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long foodId;
    private String foodItemName;

    private Integer quantity;
    private BigDecimal price; // price per item

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
