package com.baduwal.ecommerce.data.entity;

import jakarta.persistence.Embeddable;
 import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class BusinessHours {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
