package com.baduwal.ecommerce.data.dto.response;

import com.baduwal.ecommerce.data.entity.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private Long userId;
    private BigDecimal total;
    private List<CartItemDto> items;

    public static class CartItemDto {
        public Long itemId;
        public Long productId;
        public String productName;
        public Integer quantity;
        public BigDecimal totalPrice;
    }

}
