package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.dto.response.CartResponse;
import com.baduwal.ecommerce.data.entity.Delivery;
import com.baduwal.ecommerce.data.entity.Order;
import com.baduwal.ecommerce.data.entity.OrderItem;
import com.baduwal.ecommerce.data.entity.Payment;
import com.baduwal.ecommerce.data.enums.OrderStatus;
import com.baduwal.ecommerce.repo.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final NotificationService notificationService;

    public OrderService(CartService cartService,
                        OrderRepository orderRepository,
                        PaymentService paymentService,
                        DeliveryService deliveryService,
                        NotificationService notificationService) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Order placeOrder(Long userId, String paymentProvider) {

        CartResponse cart = cartService.getCart();

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }

        // Get restaurantId from first item
        int restaurantId = cart.getItems().get(0).getRestaurantId();

        // Validate all items belong to the same restaurant
        boolean sameRestaurant = cart.getItems().stream()
                .allMatch(item -> item.getRestaurantId() == restaurantId);

        if (!sameRestaurant) {
            throw new RuntimeException("Cart contains items from multiple restaurants.");
        }

        // Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .foodId(ci.getFoodItemId())
                        .foodItemName(ci.getFoodName())
                        .quantity(ci.getQuantity())
                        .price(ci.getTotalPrice())
                        .restaurantId((long) restaurantId)
                        .build()
                ).collect(Collectors.toList());

        BigDecimal totalAmount = cart.getTotal();

        // Create order
        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.ORDER_PLACED)
                .totalAmount(totalAmount)
                .items(orderItems)
                .createdAt(LocalDateTime.now())
                .build();

        // Assign order reference
        orderItems.forEach(oi -> oi.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        // Payment flow
        Payment payment = paymentService.processpayment(
                savedOrder.getOrderId(),
                totalAmount,
                paymentProvider
        );

        if (payment.getStatus().name().equals("SUCCESS")) {

            savedOrder.setOrderStatus(OrderStatus.ORDER_PLACED);
            orderRepository.save(savedOrder);

            // Delivery
            deliveryService.createDeliveryForOrder(
                    savedOrder.getOrderId(),
                    LocalDateTime.now().plusMinutes(30)
            );

            // Clear cart
            cartService.clearCart();

            // Push notification (optional)
            //notificationService.sendOrderSuccess(userId, savedOrder);

            return savedOrder;

        } else {

            savedOrder.setOrderStatus(OrderStatus.CANCELED);
            orderRepository.save(savedOrder);

            throw new RuntimeException("Payment failed!");
        }
    }
}
