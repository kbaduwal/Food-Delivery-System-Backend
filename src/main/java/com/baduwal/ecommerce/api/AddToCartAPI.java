package com.baduwal.ecommerce.api;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.managers.CartManager;
import com.baduwal.ecommerce.managers.UserManager;
import com.baduwal.ecommerce.searchers.FoodItemSearcher;
import org.springframework.stereotype.Component;

@Component
public class AddToCartAPI {
    private final UserManager userManager = new UserManager();
    private final FoodItemSearcher foodItemSearcher = new FoodItemSearcher();
    private final CartManager cartManager = new CartManager();

    public void addToCart(String userToken, int foodItemId) {
        // Validate input parameter
        if (userToken == null || userToken.isEmpty() || foodItemId < 0) {
            throw new IllegalArgumentException("Invalid user token");
         }

        //Verify user exists and token is valid
        User user = userManager.getUserByToken(userToken);
        if (user == null) {
            throw new IllegalArgumentException("User not found or Invalid user token");
        }

        //Verify food items exits
        FoodItem foodItem =foodItemSearcher.searchById(foodItemId);
        if (foodItem == null) {
            throw new IllegalArgumentException("Food Item not found with id " + foodItemId);
        }

        //Add item to the user's cart
        cartManager.addItemToCart(user, foodItem);

    }
}
