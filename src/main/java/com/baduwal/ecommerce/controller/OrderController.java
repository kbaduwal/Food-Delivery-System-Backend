package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.data.entity.Delivery;
import com.baduwal.ecommerce.data.entity.Order;
import com.baduwal.ecommerce.service.DeliveryService;
import com.baduwal.ecommerce.service.OrderService;
import com.baduwal.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final DeliveryService deliveryService;
    public OrderController(OrderService orderService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestParam Long userId,
                                            @RequestParam(defaultValue = "SIMULATED") String paymentProvider) {
        Order order = orderService.placeOrder(userId, paymentProvider);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/track")
    public ResponseEntity<Delivery> track(@PathVariable Long orderId) {
        Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
        if(delivery == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(delivery);
    }

}
