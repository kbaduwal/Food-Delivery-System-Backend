package com.baduwal.ecommerce.service;

import com.baduwal.ecommerce.data.entity.User;

public interface UserService {
    boolean registerUser(User user);
    boolean verifyOtp(String email, String otp);
    boolean setNewPassword(String email, String otp, String newPassword);
    String changePassword(String oldPassword,String newPassword,User user);
    public void sendOtp(User user);
}
