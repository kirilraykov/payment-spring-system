package com.kraykov.emerchantapp.payment.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupResponse {
    private String message;
    private String username;
    private String userEmail;
}
