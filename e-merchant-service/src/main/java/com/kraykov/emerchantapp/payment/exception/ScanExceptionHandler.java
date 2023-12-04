package com.kraykov.emerchantapp.payment.exception;

import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import com.kraykov.emerchantapp.payment.model.exception.ErrorResponse;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

@RestControllerAdvice
public class ScanExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanExceptionHandler.class);
    @ExceptionHandler(value = { CustomServiceException.class })
    public ResponseEntity<ErrorResponse> handleCustomException(CustomServiceException ex) {
        LOGGER.error("Custom client Exception caught: " + ex.getMessage());

        HttpStatus determinedHttpStatus = determineHttpStatusToReturn(ex.getErrorCode());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .errorDateTime(LocalDateTime.now())
                .status(determinedHttpStatus.toString())
                .build();

        return new ResponseEntity<>(errorResponse, determineHttpStatusToReturn(ex.getErrorCode()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class, SqlScriptException.class})
    public ErrorResponse handleSqlExceptions(Exception ex) {
        LOGGER.error("SQL Exception caught: ", ex);

        return ErrorResponse.builder()
                .errorCode(null)
                .message(ex.getCause().getMessage())
                .errorDateTime(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ErrorResponse handleParameterVerificationException(Exception ex) {
        LOGGER.error("Parameter Verification Exception caught: ", ex);

        String msg = null;
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                msg = format("Field with name: %s was rejected. Provided value: %s", fieldError.getField(),
                        fieldError.getRejectedValue());
            }
        } else {
            msg = "Automatic Fields/Argument validation failed. Please check your input.";
        }

        return ErrorResponse.builder()
                .errorCode(null)
                .message(msg)
                .errorDateTime(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    private static HttpStatus determineHttpStatusToReturn(String errorCode){
        if(isErrorCodeFormatValid(errorCode)) {
            Matcher matcher = Pattern.compile("-([0-9]{3})").matcher(errorCode);
            if(matcher.find()) {
                try {
                    return HttpStatus.valueOf(parseInt(matcher.group(1)));
                } catch (IllegalArgumentException e) {
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static boolean isErrorCodeFormatValid(String errorCode){
        String regex = "^[a-zA-Z]{4}-\\d{5}$";
        return Pattern.matches(regex, errorCode);
    }
}
