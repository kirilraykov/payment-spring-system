package com.kraykov.emerchantapp.payment.exception;

/**
 * Error codes which are specific to the handled exceptions in the Payment service.
 * To be eventually used for client error documentation.
 */
public enum ErrorCodes {
    MISSING_USER_TYPE("PMNT-40001"),
    MERCHANT_ALREADY_AUTHORIZED_TRANSACTION("PMNT-40002"),
    TRANSACTION_MISSING_AUTHORIZATION("PMNT-40003"),
    TRANSACTION_INVALID_AMOUNT("PMNT-40004"),
    TRANSACTION_ALREADY_CHARGED("PMNT-40005"),
    TRANSACTION_MISSING_PAYMENT("PMNT-40006"),
    TRANSACTION_ALREADY_REFUNDED("PMNT-40007"),
    AUTHORIZATION_REVERSED("PMNT-40008"),
    REFERENCE_ID_MISSING("PMNT-40009"),
    ACTIVE_MERCHANT_NOT_FOUND("PMNT-40401"),
    MISSING_MERCHANT_WITH_EMAIL("PMNT-40402"),
    MISSING_MERCHANT_WITH_ID("PMNT-40403"),
    MISSING_USER_FOR_USERNAME("PMNT-40404"),
    FAILED_TO_CREATE_TRANSACTION("PMNT-40001");

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
