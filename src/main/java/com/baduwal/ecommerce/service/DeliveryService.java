package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.Delivery;
import com.baduwal.ecommerce.data.enums.OrderStatus;
import com.baduwal.ecommerce.repo.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }
    public Delivery createDeliveryForOrder(Long orderId, LocalDateTime estimatedTimeOfArrival){
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .status(OrderStatus.ORDER_PLACED)
                .estimatedArrivalTime(estimatedTimeOfArrival)
                .build();
        return deliveryRepository.save(delivery);
    }

    public Delivery updateLocation(Long orderId, Double latitude, Double longitude, OrderStatus orderStatus){
        Optional<Delivery> opt = deliveryRepository.findByOrderId(orderId);
        if(opt.isPresent()) throw new RuntimeException("Delivery not found");
        Delivery delivery = opt.get();
        delivery.setCurrentLatitude(latitude);
        delivery.setCurrentLongitude(longitude);
        if (orderStatus !=null) delivery.setStatus(orderStatus);
        return deliveryRepository.save(delivery);
    }

    public Delivery getDeliveryByOrder(Long orderId){
        return deliveryRepository.findByOrderId(orderId).orElse(null);
    }

}
