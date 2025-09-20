package com.baduwal.ecommerce.controller;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import com.baduwal.ecommerce.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food-items")
public class FoodItemController {
    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping("/add-food-item")
    public ResponseEntity<FoodItem> createFoodItem(@RequestBody FoodItem foodItem) {
        return ResponseEntity.ok(foodItemService.createFoodItem(foodItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFoodItem(@PathVariable int id, @RequestBody FoodItem foodItem) {
        return ResponseEntity.ok(foodItemService.updateFoodItem(id, foodItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable int id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodItemById(@PathVariable int id) {
        return ResponseEntity.ok(foodItemService.getFoodItemById(id));
    }

    @GetMapping("/get-all-food-item")
    public ResponseEntity<List<FoodItem>> getAllFoodItems() {
        return ResponseEntity.ok(foodItemService.getAllFoodItems());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByRestaurant(@PathVariable int restaurantId) {
        return ResponseEntity.ok(foodItemService.getFoodItemsByRestaurant(restaurantId));
    }

    @GetMapping("/meal/{mealType}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByMealType(@PathVariable MealType mealType) {
        return ResponseEntity.ok(foodItemService.getFoodItemsByMealType(mealType));
    }

    @GetMapping("cuisine/{cuisineType}")
    public ResponseEntity<List<FoodItem>> getFoodItemsByCuisineType(@PathVariable CuisineType cuisineType) {
        return ResponseEntity.ok(foodItemService.getFoodItemsByCuisineType(cuisineType));
    }

    @GetMapping("/available")
    public ResponseEntity<List<FoodItem>> getAvailableFoodItems(){
        return ResponseEntity.ok(foodItemService.getAvailableFoodItems());
    }

}
