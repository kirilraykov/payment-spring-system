package com.kraykov.emerchantapp.payment.service.impl;

import com.kraykov.emerchantapp.payment.model.user.User;
import com.kraykov.emerchantapp.payment.model.user.UserSignupResponse;
import com.kraykov.emerchantapp.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserSignupResponse registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return UserSignupResponse.builder()
                .username(user.getUsername())
                .userEmail(user.getEmail())
                .message(format("New user with username: %s was created. Please login to continue.", user.getUsername()))
                .build();
    }
}
