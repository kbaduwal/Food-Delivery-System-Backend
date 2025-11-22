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

    List<FoodItem> findByMealType(MealType mealType);

    List<FoodItem> findByCuisineType(CuisineType cuisineType);

    List<FoodItem> findByIsAvailableTrue();
}
