package com.baduwal.ecommerce.data.entity;


import com.baduwal.ecommerce.data.enums.CuisineType;
import com.baduwal.ecommerce.data.enums.MealType;
import com.baduwal.ecommerce.data.enums.StarRating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    //Implement using Builder pattern later as we have too many parameter

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    private CuisineType cuisineType;

    @Enumerated(EnumType.STRING)
    private StarRating starRating;

    private int restaurantId;
    private boolean isAvailable;

}
