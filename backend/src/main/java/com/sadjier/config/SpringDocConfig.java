package com.sadjier.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    //配置相应状态状态码(暂无实现)
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI().components(new io.swagger.v3.oas.models.Components());
//    }
//    @Bean
//    public OpenApiCustomizer globalResponseCustomizer() {
//        return openApi -> {
//            Map<String, ApiResponse> globalResponses = openApi.getComponents().getResponses();
//
//            openApi.getPaths().values().forEach(pathItem -> {
//                pathItem.readOperations().forEach(operation -> {
//                    globalResponses.forEach((code, response) -> {
//                        if (!operation.getResponses().containsKey(code)) {
//                            operation.getResponses().addApiResponse(code, response);
//                        }
//                    });
//                });
//            });
//        };
//    }
}