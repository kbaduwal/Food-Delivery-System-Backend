package com.baduwal.ecommerce.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    private String phoneNumber;
    private String email;

    private String password;
    private boolean isVerified;
    private String otp;
    private LocalDateTime tokenExpiration;

    //Just for auth Service demo purpose
    public User(String demoUser, String mail, String password) {
        this.name = demoUser;
        this.email = mail;
        this.password = password;

    }
}
