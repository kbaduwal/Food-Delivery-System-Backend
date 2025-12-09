package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.dto.request.AddToCartRequest;
import com.baduwal.ecommerce.data.dto.response.CartResponse;
import com.baduwal.ecommerce.data.entity.Cart;
import com.baduwal.ecommerce.data.entity.CartItem;
import com.baduwal.ecommerce.data.entity.FoodItem;
import com.baduwal.ecommerce.data.entity.User;
import com.baduwal.ecommerce.repo.CartItemRepository;
import com.baduwal.ecommerce.repo.CartRepository;
import com.baduwal.ecommerce.repo.FoodItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final AuthService authService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            FoodItemRepository foodItemRepository,
            AuthService authService
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.authService = authService;
    }

    // --------------------------------------------------
    // ADD TO CART
    // --------------------------------------------------
    @Transactional
    public Cart addCart(AddToCartRequest request) {

        User user = authService.getAuthenticatedUser();

        // Get or create cart
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() ->
                cartRepository.save(new Cart(user))
        );

        FoodItem foodItem = foodItemRepository.findById(request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        // Prevent mixing restaurants (optional)
        if (!cart.getItems().isEmpty()) {
            int existingRestaurant = cart.getItems().get(0).getFood().getRestaurant().getId();
            if (existingRestaurant != foodItem.getRestaurant().getId()) {
                throw new RuntimeException("All cart items must be from the same restaurant.");
            }
        }

        CartItem item = cartItemRepository.findByCartCartIdAndFoodId(cart.getCartId(), foodItem.getId())
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.recalc();
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(foodItem, request.getQuantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.recalcTotal();
        return cartRepository.save(cart);
    }

    // --------------------------------------------------
    // GET CART
    // --------------------------------------------------
    @Transactional
    public CartResponse getCart() {
        User user = authService.getAuthenticatedUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> new Cart(user));

        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setUserId(user.getId());
        response.setTotal(cart.getTotal());

        response.setItems(
                cart.getItems().stream().map(it -> {
                    CartResponse.CartItemDto dto = new CartResponse.CartItemDto();
                    dto.itemId = it.getId();
                    dto.foodItemId = it.getFood().getId();
                    dto.foodName = it.getFood().getName();
                    dto.quantity = it.getQuantity();
                    dto.totalPrice = it.getTotalPrice();
                    dto.restaurantId = it.getFood().getRestaurant().getId();   // <-- FIXED
                    return dto;
                }).collect(Collectors.toList())
        );

        return response;
    }

    // --------------------------------------------------
    // CLEAR CART
    // --------------------------------------------------
    public void clearCart() {
        User user = authService.getAuthenticatedUser();
        cartRepository.deleteByUserId(user.getId()); // delete only user's cart
    }
}
