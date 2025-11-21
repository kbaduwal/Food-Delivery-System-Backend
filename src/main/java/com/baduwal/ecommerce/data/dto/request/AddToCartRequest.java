package com.baduwal.ecommerce.data.dto.request;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long foodId;
    private Integer quantity=1;
}
