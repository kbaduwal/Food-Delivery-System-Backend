package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.data.entity.Order;
import com.baduwal.ecommerce.data.entity.Restaurant;
import com.baduwal.ecommerce.repo.OrderRepository;
import com.baduwal.ecommerce.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/add-restaurant")
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.addRestaurant(restaurant));
    }

    @GetMapping("/{resId}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable int resId) {
        return restaurantService.getRestaurantById(resId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllRestaurant")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @DeleteMapping("/id")
    public ResponseEntity<String> deleteRestaurantById(@PathVariable int id) {
        boolean deleted = restaurantService.deleteRestaurantById(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted restaurant with id: " + id);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long id){
        List<Order> orders = orderRepository.findByRestaurantId(id);
        return ResponseEntity.ok(orders);
    }

}
