package com.kraykov.emerchantapp.payment.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignupResponse {
    private String message;
    private String username;
    private String userEmail;
}
