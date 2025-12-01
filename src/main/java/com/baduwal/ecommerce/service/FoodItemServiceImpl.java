package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import com.baduwal.ecommerce.repo.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Override
    public FoodItem createFoodItem(FoodItem foodItem, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String path = "uploads/" + file.getOriginalFilename();
            File dest = new File(path);
            dest.getParentFile().mkdirs();
            file.transferTo(dest);
            foodItem.setImageUrl(path);
        }
        return foodItemRepository.save(foodItem);
    }

    @Override
    public FoodItem updateFoodItem(Long id, FoodItem foodItem) {
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
    public void deleteFoodItem(Long id) {
        foodItemRepository.deleteById(id);
    }

    @Override
    public Optional<FoodItem> getFoodItemById(Long id) {
        return foodItemRepository.findById(id);
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

    @Override
    public List<FoodItem> searchFoodItems(String keyword) {
        return foodItemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<FoodItem> filterFoodItems(Integer restaurantId, Long categoryId, CuisineType cuisineType, MealType mealType) {
        if (restaurantId != null){
            if (mealType != null && cuisineType != null) {
                return foodItemRepository.findByRestaurantIdAndMealTypeAndCuisineType(restaurantId,mealType,cuisineType);
            }
            if (mealType != null){
                return foodItemRepository.findByRestaurantIdAndMealType(restaurantId, mealType);
            }
            if (cuisineType != null) {
                return foodItemRepository.findByRestaurantIdAndCuisineType(restaurantId, cuisineType);
            }
            return foodItemRepository.findByRestaurantId(restaurantId);
        }

        if (categoryId != null) {
            if (cuisineType != null && mealType != null) {
                return foodItemRepository.findByCategoryIdAndCuisineTypeAndMealType(categoryId, cuisineType, mealType);
            }
            if (cuisineType != null) {
                return foodItemRepository.findByCategoryIdAndCuisineType(categoryId, cuisineType);
            }
            return foodItemRepository.findByCategoryId(categoryId);
        }

        return foodItemRepository.findAll();
    }

}
