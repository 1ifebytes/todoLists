package com.sleekflow.interfaces.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 错误响应类
 * <p>
 * Error Response Class
 * </p>
 * <p>
 * API 错误响应的统一格式，由 GlobalExceptionHandler 返回。
 * </p>
 * <p>
 * Unified format for API error responses, returned by GlobalExceptionHandler.
 * </p>
 * <p>
 * <b>响应格式（Response Format）：</b></p>
 * <pre>
 * {
 *   "success": false,
 *   "error": "ERROR_CODE",
 *   "message": "Human readable error message",
 *   "timestamp": "2025-01-15T10:30:00"
 * }
 * </pre>
 * <p>
 * <b>错误代码示例（Error Code Examples）：</b></p>
 * <ul>
 *   <li>RESOURCE_NOT_FOUND - 资源未找到 / Resource not found</li>
 *   <li>FORBIDDEN - 禁止访问 / Access forbidden</li>
 *   <li>DUPLICATE_RESOURCE - 资源重复 / Duplicate resource</li>
 *   <li>VALIDATION_FAILED - 验证失败 / Validation failed</li>
 *   <li>UNAUTHORIZED - 未授权 / Unauthorized</li>
 *   <li>INVALID_REQUEST - 请求格式错误 / Invalid request format</li>
 *   <li>INTERNAL_SERVER_ERROR - 服务器内部错误 / Internal server error</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Builder
public class ErrorResponse {

    /**
     * 请求是否成功（固定为 false）
     * <p>
     * Request success status (always false)
     * </p>
     */
    @Builder.Default
    private final boolean success = false;

    /**
     * 错误代码
     * <p>
     * Error code
     * </p>
     * <p>
     * 用于程序化处理错误类型的标识符。
     * </p>
     * <p>
     * Identifier for programmatic error type handling.
     * </p>
     */
    private final String error;

    /**
     * 错误消息
     * <p>
     * Error message
     * </p>
     * <p>
     * 人类可读的错误描述。
     * </p>
     * <p>
     * Human-readable error description.
     * </p>
     */
    private final String message;

    /**
     * 时间戳
     * <p>
     * Timestamp
     * </p>
     * <p>
     * 错误发生的时间（ISO 8601 格式）。
     * </p>
     * <p>
     * Time when error occurred (ISO 8601 format).
     * </p>
     */
    private final String timestamp;
}
