package com.baduwal.ecommerce.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_item_id")
    private FoodItem food;

    private Integer quantity = 0;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public CartItem() {}
    public CartItem(FoodItem food, Integer quantity) {
        this.food = food;
        this.quantity = quantity;
        recalc();
    }

    public void recalc() {
        if (food != null && food.getPrice() !=null) {
            this.totalPrice = food.getPrice().multiply(new BigDecimal(quantity));
        }else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }

}
