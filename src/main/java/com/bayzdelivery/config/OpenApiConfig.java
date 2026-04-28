package com.bayzdelivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for API documentation.
 *
 * @author Omar Ismail
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bayzDeliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BayzDelivery API")
                        .description("REST API for the BayzDelivery delivery management system")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Omar Ismail")
                                .email("omar.ismail@bayzdelivery.com")));
    }
}
