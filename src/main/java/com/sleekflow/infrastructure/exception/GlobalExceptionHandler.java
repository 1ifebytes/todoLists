package com.sleekflow.infrastructure.exception;

import com.sleekflow.interfaces.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * Global Exception Handler
 * </p>
 * <p>
 * 统一处理应用中抛出的所有异常，返回标准化的错误响应。
 * </p>
 * <p>
 * Handles all exceptions thrown in the application uniformly, returning standardized error responses.
 * </p>
 * <p>
 * <b>处理的异常类型（Handled Exception Types）：</b></p>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → HTTP 404 NOT_FOUND</li>
 *   <li>{@link ForbiddenException} → HTTP 403 FORBIDDEN</li>
 *   <li>{@link DuplicateResourceException} → HTTP 409 CONFLICT</li>
 *   <li>{@link MethodArgumentNotValidException} → HTTP 400 BAD_REQUEST（Bean Validation 失败）/ Bean Validation failure</li>
 *   <li>{@link BadCredentialsException} → HTTP 401 UNAUTHORIZED（登录凭证无效）/ Invalid login credentials</li>
 *   <li>{@link DataIntegrityViolationException} → HTTP 409 CONFLICT（数据库唯一约束冲突）/ Database unique constraint violation</li>
 *   <li>{@link HttpMessageNotReadableException} → HTTP 400 BAD_REQUEST（JSON 格式错误）/ Malformed JSON</li>
 *   <li>{@link Exception} → HTTP 500 INTERNAL_SERVER_ERROR（通用异常捕获）/ Generic exception catch-all</li>
 * </ul>
 * <p>
 * <b>安全说明（Security Note）：</b></p>
 * <p>
 * 通用异常处理器不会泄露内部实现细节，返回通用错误消息。
 * DataIntegrityViolationException 处理器将高并发场景下的唯一约束冲突转换为 409 状态码，防止返回 500。
 * </p>
 * <p>
 * The catch-all exception handler never leaks internal details, returns a generic error message.
 * The DataIntegrityViolationException handler converts unique constraint violations in high-concurrency scenarios
 * to 409 status codes, preventing 500 errors.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理资源未找到异常
     * <p>
     * Handle resource not found exception
     * </p>
     *
     * @param ex 资源未找到异常 / Resource not found exception
     * @return HTTP 404 响应 / HTTP 404 response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("RESOURCE_NOT_FOUND", ex.getMessage()));
    }

    /**
     * 处理禁止访问异常
     * <p>
     * Handle forbidden exception
     * </p>
     *
     * @param ex 禁止访问异常 / Forbidden exception
     * @return HTTP 403 响应 / HTTP 403 response
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("FORBIDDEN", ex.getMessage()));
    }

    /**
     * 处理重复资源异常
     * <p>
     * Handle duplicate resource exception
     * </p>
     *
     * @param ex 重复资源异常 / Duplicate resource exception
     * @return HTTP 409 响应 / HTTP 409 response
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("DUPLICATE_RESOURCE", ex.getMessage()));
    }

    /**
     * 处理 Bean Validation 失败异常
     * <p>
     * Handle bean validation failure exception
     * </p>
     * <p>
     * 收集所有字段错误到一个消息字符串中。
     * </p>
     * <p>
     * Collects all field errors into one message string.
     * </p>
     *
     * @param ex 方法参数验证异常 / Method argument not valid exception
     * @return HTTP 400 响应 / HTTP 400 response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("VALIDATION_FAILED", message));
    }

    /**
     * 处理登录凭证无效异常
     * <p>
     * Handle bad credentials exception
     * </p>
     *
     * @param ex 凭证无效异常 / Bad credentials exception
     * @return HTTP 401 响应 / HTTP 401 response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("UNAUTHORIZED", "Invalid credentials"));
    }

    /**
     * 处理数据完整性冲突异常
     * <p>
     * Handle data integrity violation exception
     * </p>
     * <p>
     * 捕获数据库唯一约束冲突（如并发场景下的重复数据），返回 409 状态码。
     * </p>
     * <p>
     * Catches database unique constraint violations (e.g., duplicate data in concurrent scenarios), returns 409 status code.
     * </p>
     *
     * @param ex 数据完整性冲突异常 / Data integrity violation exception
     * @return HTTP 409 响应 / HTTP 409 response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Check if root cause is a constraint violation (unique constraint conflict)
        if (ex.getRootCause() instanceof ConstraintViolationException ||
                (ex.getCause() != null && ex.getCause().getCause() instanceof ConstraintViolationException)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(error("DUPLICATE_RESOURCE", "Resource already exists"));
        }
        // Other data integrity issues
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("DATABASE_ERROR", "Database operation failed"));
    }

    /**
     * 处理 JSON 格式错误异常
     * <p>
     * Handle malformed JSON exception
     * </p>
     *
     * @param ex HTTP 消息不可读异常 / HTTP message not readable exception
     * @return HTTP 400 响应 / HTTP 400 response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("INVALID_REQUEST", "Malformed JSON request body"));
    }

    /**
     * 处理通用异常（兜底处理）
     * <p>
     * Handle generic exception (catch-all)
     * </p>
     * <p>
     * 捕获所有未处理的异常，绝不泄露内部细节。
     * </p>
     * <p>
     * Catches all unhandled exceptions, never leaks internal details.
     * </p>
     *
     * @param ex 异常 / Exception
     * @return HTTP 500 响应 / HTTP 500 response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }

    /**
     * 构建错误响应对象
     * <p>
     * Build error response object
     * </p>
     *
     * @param code 错误代码 / Error code
     * @param message 错误消息 / Error message
     * @return 错误响应 / Error response
     */
    private ErrorResponse error(String code, String message) {
        return ErrorResponse.builder()
                .error(code)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
