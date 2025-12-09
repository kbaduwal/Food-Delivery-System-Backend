package com.baduwal.ecommerce.permissions;

import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.entity.Restaurant;
import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.managers.DeliveryManager;
import com.baduwal.ecommerce.searchers.RestaurantSearcher;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartPermission implements Permission{
    private final User user;
    private final FoodItem foodItem;
    private final DeliveryManager deliveryManager;

    public AddToCartPermission(User user, FoodItem foodItem) {
        this.user = user;
        this.foodItem = foodItem;
        deliveryManager = new DeliveryManager();
    }

    @Override
    public boolean isPermitted() {
        if(!foodItem.isAvailable()){
            return false;
        }

        Restaurant restaurant = new RestaurantSearcher().searchById(foodItem.getRestaurant().getId());

        return deliveryManager.isDeliveryPossible(restaurant.getAddress(),user.getAddress());
    }
}
