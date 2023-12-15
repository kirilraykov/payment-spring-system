package com.kraykov.emerchantapp.payment.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kraykov.emerchantapp.payment.model.exception.CustomServiceException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;

import static com.kraykov.emerchantapp.payment.exception.ErrorCodes.FAILED_TO_PARSE_AUTH_TOKEN;

public class ParseHeaderContents {

    public static String parseUserEmailFromAuth() {
        String bearerAuthorization = getAccessTokenFromAuthorization();

        String[] chunks = bearerAuthorization.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonObject = mapper.readValue(payload, JsonNode.class);
            return jsonObject.get("userEmail").textValue();
        } catch (JsonProcessingException e) {
            throw new CustomServiceException(FAILED_TO_PARSE_AUTH_TOKEN.getCode(),
                    "Failed to parse auth token: " + e.getMessage());
        }
    }

    private static String getAccessTokenFromAuthorization() {
        RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attribs).getRequest();
        return request.getHeader("Authorization");
    }
}
