package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.data.dto.request.AddToCartRequest;
import com.baduwal.ecommerce.data.dto.response.CartResponse;
import com.baduwal.ecommerce.data.entity.Cart;
import com.baduwal.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

   private final CartService cartService;

   public CartController(CartService cartService) {
       this.cartService = cartService;
   }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        Cart cart = cartService.addCart(request);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("")
    public ResponseEntity<CartResponse> getCart() {
       return ResponseEntity.ok(cartService.getCart());
    }
}
