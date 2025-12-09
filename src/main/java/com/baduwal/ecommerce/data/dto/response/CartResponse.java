package com.baduwal.ecommerce.data.dto.response;

import com.baduwal.ecommerce.data.entity.CartItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private Long userId;
    private BigDecimal total;
    private List<CartItemDto> items;

    @Getter
    @Setter
    public static class CartItemDto {
        public Long itemId;
        public Long foodItemId;
        public String foodName;
        public int quantity;
        public BigDecimal totalPrice;

        public int restaurantId;   // <-- REQUIRED FOR ORDER SERVICE
    }

}
