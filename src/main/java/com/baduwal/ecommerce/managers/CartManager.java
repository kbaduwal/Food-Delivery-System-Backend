package com.baduwal.ecommerce.managers;

import com.baduwal.ecommerce.data.DataAccessObjectConverter;
import com.baduwal.ecommerce.data.DataAccessResult;
import com.baduwal.ecommerce.data.DataAccessor;
import com.baduwal.ecommerce.data.entity.CartItem;
import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.factory.PermissionFactory;
import com.baduwal.ecommerce.permissions.Permission;

import java.util.List;

public class CartManager {

    public List<CartItem> getUserCart(User user){
        DataAccessResult dataAccessResult = DataAccessor.getUserCart(user);
        return DataAccessObjectConverter.convertToCartItems(dataAccessResult);
    }

    // adds 1 unit
    public void addItemToCart(User user, FoodItem foodItem){
        Permission permission = PermissionFactory.getAddToCartPermission(user, foodItem);

        if(!permission.isPermitted()){
            throw new RuntimeException("Permission Denied");
        }

        if(!isFoodItemFromSameRestaurant(user, foodItem)){
            throw new RuntimeException("Cart Item from different restaurant...");
        }

        DataAccessor.addItemToCart(user, foodItem);

    }

    public void deleteItemFromCart(User user, FoodItem foodItem){
        Permission permission = PermissionFactory.getDeleteFromCartPermission(user, foodItem);
        if(!permission.isPermitted()){
            throw new RuntimeException("Permission Denied");
        }
        if(!isFoodItemPresentInCart(user,foodItem)){
            throw new RuntimeException("Food Item not present in cart...");
        }

        DataAccessor.deleteItemFromCart(user,foodItem);


    }

    public void checkoutUserCart(User user){
        Permission permission = PermissionFactory.getCheckOutCartPermission(user);
        if(!permission.isPermitted()){
            throw new RuntimeException("Permission Denied");
        }
        if(isCartEmpty(user)){
            throw new RuntimeException("Cart Empty");
        }

        DataAccessor.checkOutCart(user);

    }

    private boolean isFoodItemFromSameRestaurant(User user, FoodItem foodItem) {
        List<CartItem> cartItems = getUserCart(user);

        return cartItems.isEmpty() ||
                cartItems.get(0).getFood().getRestaurant().equals(foodItem.getRestaurant());
    }

    private boolean isFoodItemPresentInCart(User user, FoodItem foodItem){
        List<CartItem> cartItems = getUserCart(user);
        for (CartItem item : cartItems) {
            if(item.getFood().getId()==foodItem.getId()){
                return true;
            }
        }
        return false;
    }

    private boolean isCartEmpty(User user){
        List<CartItem> cartItems = getUserCart(user);
        return cartItems.isEmpty();
    }

}
