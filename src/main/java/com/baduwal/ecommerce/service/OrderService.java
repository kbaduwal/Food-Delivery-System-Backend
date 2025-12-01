package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.dto.response.CartResponse;
import com.baduwal.ecommerce.data.entity.Order;
import com.baduwal.ecommerce.data.entity.OrderItem;
import com.baduwal.ecommerce.data.enums.OrderStatus;
import com.baduwal.ecommerce.repo.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartService cartService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderService(CartService cartService, OrderService orderService, OrderRepository orderRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(Long userId) {
        CartResponse cart = cartService.getCart();

        if(cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        //Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci->OrderItem.builder()
                        .foodId(ci.foodItemId)
                        .foodItemName(ci.foodName)
                        .quantity(ci.quantity)
                        .price(ci.totalPrice)
                        .build()
                ).collect(Collectors.toList());

        BigDecimal totalAmount = cart.getTotal();

        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.COOKING)
                .totalAmount(totalAmount)
                .items(orderItems)
                .build();

        //Assign order reference to each item
        orderItems.forEach(oi->oi.setOrder(order));

        // save order
        Order savedOrder = orderRepository.save(order);

        // clear the cart
        cartService.clearCart();

        return savedOrder;
    }
}
