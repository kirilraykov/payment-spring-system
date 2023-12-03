package com.kraykov.emerchantapp.payment.model.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ErrorResponse {
   private final String message;
   private final String status;
   private final String errorCode;
   private final LocalDateTime errorDateTime;
}
