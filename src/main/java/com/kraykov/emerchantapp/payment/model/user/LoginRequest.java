package com.kraykov.emerchantapp.payment.model.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginRequest {
    String username;
    String password;
}
