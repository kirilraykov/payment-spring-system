package com.kraykov.emerchantapp.payment.exception;
public enum ErrorCodes {
    MISSING_USER_TYPE("PMNT-40001");

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
