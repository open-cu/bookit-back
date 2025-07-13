package com.opencu.bookit.adapter.in.web.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bookit Coworking API",
                version = "v1",
                description = "API for coworking space booking"
        )
)
public class OpenApiConfig {

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            MediaType schema = new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError"));
            responses.addApiResponse("400", new ApiResponse()
                    .description("Validation error")
                    .content(new Content().addMediaType("application/json", schema)));
            responses.addApiResponse("403", new ApiResponse()
                    .description("Access forbidden")
                    .content(new Content().addMediaType("application/json", schema)));
            responses.addApiResponse("404", new ApiResponse()
                    .description("Resource not found")
                    .content(new Content().addMediaType("application/json", schema)));
            responses.addApiResponse("500", new ApiResponse()
                    .description("Internal server error")
                    .content(new Content().addMediaType("application/json", schema)));
            return operation;
        };
    }
}