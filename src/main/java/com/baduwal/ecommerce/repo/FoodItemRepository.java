package com.baduwal.ecommerce.repo;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByRestaurantId(Integer restaurantId);
    List<FoodItem> findByRestaurantIdAndMealType(Integer restaurantId, MealType mealType);
    List<FoodItem> findByRestaurantIdAndCuisineType(Integer restaurantId, CuisineType cuisineType);
    List<FoodItem> findByRestaurantIdAndMealTypeAndCuisineType(Integer restaurantId, MealType mealType, CuisineType cuisineType);

    List<FoodItem> findByCategoryId(Long categoryId);
    List<FoodItem> findByCategoryIdAndCuisineType(Long categoryId, CuisineType cuisineType);
    List<FoodItem> findByCategoryIdAndCuisineTypeAndMealType(Long categoryId, CuisineType cuisineType, MealType mealType);


    List<FoodItem> findByMealType(MealType mealType);

    List<FoodItem> findByCuisineType(CuisineType cuisineType);

    List<FoodItem> findByIsAvailableTrue();

    List<FoodItem> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
