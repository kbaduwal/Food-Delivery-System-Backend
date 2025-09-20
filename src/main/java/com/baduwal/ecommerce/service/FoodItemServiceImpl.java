package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import com.baduwal.ecommerce.repo.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Override
    public FoodItem createFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    @Override
    public FoodItem updateFoodItem(int id, FoodItem foodItem) {
        FoodItem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("FoodItem not found"));
        existingFoodItem.setName(foodItem.getName());
        existingFoodItem.setDescription(foodItem.getDescription());
        existingFoodItem.setPrice(foodItem.getPrice());
        existingFoodItem.setMealType(foodItem.getMealType());
        existingFoodItem.setCuisineType(foodItem.getCuisineType());
        existingFoodItem.setStarRating(foodItem.getStarRating());
        existingFoodItem.setAvailable(foodItem.isAvailable());
        return foodItemRepository.save(existingFoodItem);
    }

    @Override
    public void deleteFoodItem(int id) {
        foodItemRepository.deleteById(id);
    }

    @Override
    public FoodItem getFoodItemById(int id) {
        return foodItemRepository.findById(id).
                orElseThrow(()-> new RuntimeException("FoodItem not found"));
    }

    @Override
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    @Override
    public List<FoodItem> getFoodItemsByRestaurant(int restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<FoodItem> getFoodItemsByMealType(MealType mealType) {
        return foodItemRepository.findByMealType(mealType);
    }

    @Override
    public List<FoodItem> getFoodItemsByCuisineType(CuisineType cuisineType) {
        return foodItemRepository.findByCuisineType(cuisineType);
    }

    @Override
    public List<FoodItem> getAvailableFoodItems() {
        return foodItemRepository.findByIsAvailableTrue();
    }
}
