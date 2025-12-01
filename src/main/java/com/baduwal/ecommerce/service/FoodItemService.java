package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FoodItemService {
    FoodItem createFoodItem(FoodItem foodItem, MultipartFile file)throws IOException;
    FoodItem updateFoodItem(Long id, FoodItem foodItem);
    void deleteFoodItem(Long id);
    Optional<FoodItem> getFoodItemById(Long id);
    List<FoodItem> getAllFoodItems();
    List<FoodItem> getFoodItemsByRestaurant(int restaurantId);
    List<FoodItem> getFoodItemsByMealType(MealType mealType);
    List<FoodItem> getFoodItemsByCuisineType(CuisineType cuisineType);
    List<FoodItem> getAvailableFoodItems();

    List<FoodItem> searchFoodItems(String keyword);
    List<FoodItem> filterFoodItems(Integer restaurantId, Long categoryId, CuisineType cuisineType, MealType mealType);

}
