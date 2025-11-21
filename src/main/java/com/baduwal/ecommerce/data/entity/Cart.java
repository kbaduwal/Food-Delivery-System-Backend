package com.baduwal.ecommerce.data.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();

    private BigDecimal total = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Cart() {}
    public Cart(User user) {
        this.user = user;
    }

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        recalcTotal();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        recalcTotal();
    }

    public void recalcTotal() {
        this.total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
