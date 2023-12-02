package com.kraykov.emerchantapp.payment.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger3 configure
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openApiConfig() {
        return new OpenAPI()
                .info(new Info().title("Payment system REST API Documentation")
                        .description("This is Swagger REST API Docs for Example payment system using Spring.")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Github Project")
                        .url("https://github.com/kirilraykov/payment-spring-system/tree/main"));
    }
}
