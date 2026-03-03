package com.sleekflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Todo List API 应用程序主类
 * <p>
 * Todo List API Main Application Class
 * </p>
 * <p>
 * SleekFlow TODO List API 的 Spring Boot 应用程序入口点。
 * 负责引导应用程序并启动嵌入式服务器。
 * </p>
 * <p>
 * Spring Boot application entry point for SleekFlow TODO List API.
 * Bootstraps the application and starts the embedded server.
 * </p>
 * <p>
 * <b>技术栈（Tech Stack）：</b></p>
 * <ul>
 *   <li>Java 21</li>
 *   <li>Spring Boot 3.3.5</li>
 *   <li>MySQL 9.5.0</li>
 *   <li>Spring Data JPA (Hibernate)</li>
 *   <li>Spring Security + JWT</li>
 *   <li>MapStruct</li>
 *   <li>Flyway</li>
 * </ul>
 * <p>
 * <b>架构（Architecture）：</b></p>
 * <p>
 * Domain-Driven Design (DDD) 4-layer architecture:
 * </p>
 * <ul>
 *   <li>interfaces/ — REST Controllers + DTOs + Assemblers</li>
 *   <li>application/ — Use cases and business logic orchestration</li>
 *   <li>domain/ — Business entities and value objects</li>
 *   <li>infrastructure/ — Persistence, security, and external integrations</li>
 * </ul>
 * <p>
 * <b>启动方式（How to Run）：</b></p>
 * <pre>
 * # Maven
 * mvn spring-boot:run
 *
 * # Docker
 * docker compose up --build
 *
 * # IDE (IDEA)
 * Right-click TodoListApplication.java → Run
 * </pre>
 * <p>
 * <b>访问地址（Access URLs）：</b></p>
 * <ul>
 *   <li>API: http://localhost:8080/api/v1</li>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>Health Check: http://localhost:8080/actuator/health</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@SpringBootApplication
public class TodoListApplication {

    /**
     * 应用程序主入口
     * <p>
     * Application main entry point
     * </p>
     * <p>
     * 启动 Spring Boot 应用程序。
     * </p>
     * <p>
     * Starts the Spring Boot application.
     * </p>
     *
     * @param args 命令行参数 / Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }
}
