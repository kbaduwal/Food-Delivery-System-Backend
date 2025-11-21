package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.api.AddToCartAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class AddToCartController {

    @Autowired
    private AddToCartAPI addToCartAPI;

    public ResponseEntity<>
}
