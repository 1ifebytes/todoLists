package com.sleekflow.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 配置类
 * <p>
 * OpenAPI / Swagger Configuration Class
 * </p>
 * <p>
 * 配置 OpenAPI 3.0 文档生成，用于自动生成 API 文档和 Swagger UI。
 * </p>
 * <p>
 * Configures OpenAPI 3.0 documentation generation for automatic API documentation and Swagger UI.
 * </p>
 * <p>
 * <b>访问地址（Access URLs）：</b></p>
 * <ul>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>OpenAPI JSON: http://localhost:8080/v3/api-docs</li>
 *   <li>API Docs: http://localhost:8080/api-docs</li>
 * </ul>
 * <p>
 * <b>JWT 认证配置（JWT Authentication Configuration）：</b></p>
 * <p>
 * Swagger UI 中所有受保护的端点都会显示锁图标🔒，可以通过 "Authorize" 按钮输入 JWT 令牌。
 * </p>
 * <p>
 * All protected endpoints in Swagger UI show a lock icon🔒, and you can enter the JWT token via the "Authorize" button.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bearer 认证方案常量
     * <p>
     * Bearer authentication scheme constant
     * </p>
     */
    private static final String BEARER_AUTH = "Bearer Auth";

    /**
     * 配置 OpenAPI 自定义 Bean
     * <p>
     * Configure OpenAPI custom bean
     * </p>
     * <p>
     * 定义 API 的元信息、JWT 认证方案和安全要求。
     * </p>
     * <p>
     * Defines API metadata, JWT authentication scheme, and security requirements.
     * </p>
     * <p>
     * <b>主要配置（Key Configuration）：</b></p>
     * <ul>
     *   <li>API 标题：SleekFlow TODO List API</li>
     *   <li>API 版本：1.0.0</li>
     *   <li>认证方案：Bearer JWT（HTTP Bearer scheme）</li>
     *   <li>安全要求：所有端点默认需要 Bearer 认证 / All endpoints require Bearer auth by default</li>
     * </ul>
     *
     * @return OpenAPI 配置对象 / OpenAPI configuration object
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // API 信息（标题、描述、版本）/ API info (title, description, version)
                .info(new Info()
                        .title("SleekFlow TODO List API")
                        .description("REST API with JWT authentication, RBAC, and activity feed")
                        .version("1.0.0"))
                // 为所有受保护的端点添加锁图标 / Adds the lock icon to all protected endpoints in Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                // 定义 Bearer JWT 认证方案 / Define Bearer JWT authentication scheme
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
