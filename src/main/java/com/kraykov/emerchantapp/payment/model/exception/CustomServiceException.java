package com.kraykov.emerchantapp.payment.model.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomServiceException extends RuntimeException {

    private static final long serialVersionUID = 7535293903239773113L;

    private String errorMessage;
    private String errorCode;

    public CustomServiceException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String messageFormat(Object... args) {
        return String.format(this.getMessage(), args);
    }
}
