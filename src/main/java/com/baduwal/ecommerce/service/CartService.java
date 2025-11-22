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

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       FoodItemRepository foodItemRepository,
                       AuthService authService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.authService = authService;
    }

    @Transactional
    public Cart addCart(AddToCartRequest request) {
        User user = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(
                () ->{
                    Cart c = new Cart(user);
                return cartRepository.save(c);
                 }
        );

        FoodItem foodItem = foodItemRepository.findById(request.getFoodId())
                .orElseThrow(()-> new RuntimeException("Food not found"));

        CartItem item = cartItemRepository.findByCartIdAndFoodId(cart.getCartId(),foodItem.getId())
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.recalc();
            cartItemRepository.save(item);
        }else {
            CartItem newItem = new CartItem(foodItem, request.getQuantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.recalcTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public CartResponse getCart() {
        User user = authService.getAuthenticatedUser();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(()->new Cart(user));

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(cart.getCartId());
        cartResponse.setUserId(user.getId());
        cartResponse.setTotal(cart.getTotal());
        cartResponse.setItems(cart.getItems().stream().map(it->{
            CartResponse.CartItemDto cartItemDto = new CartResponse.CartItemDto();
            cartItemDto.itemId = it.getCartItemId();
            cartItemDto.foodItemId = it.getFood().getId();
            cartItemDto.foodName = it.getFood().getName();
            cartItemDto.quantity = it.getQuantity();
            cartItemDto.totalPrice = it.getTotalPrice();
            return cartItemDto;

        }).collect(Collectors.toList()));

        return cartResponse;
    }

}
