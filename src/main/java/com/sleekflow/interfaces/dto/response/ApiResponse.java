package com.sleekflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一 API 响应包装类
 * <p>
 * Unified API Response Wrapper
 * </p>
 * <p>
 * 所有 API 成功响应的统一包装格式。
 * </p>
 * <p>
 * Unified wrapper format for all successful API responses.
 * </p>
 * <p>
 * <b>响应格式（Response Format）：</b></p>
 * <pre>
 * {
 *   "success": true,
 *   "data": { ... },
 *   "message": "Optional message"
 * }
 * </pre>
 *
 * @param <T> 数据类型 / Data type
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 请求是否成功
     * <p>
     * Request success status
     * </p>
     */
    private boolean success;

    /**
     * 响应数据
     * <p>
     * Response data
     * </p>
     */
    private T data;

    /**
     * 可选消息
     * <p>
     * Optional message
     * </p>
     */
    private String message;

    /**
     * 创建成功响应（无消息）
     * <p>
     * Create success response (without message)
     * </p>
     *
     * @param data 响应数据 / Response data
     * @param <T> 数据类型 / Data type
     * @return API 响应 / API response
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 创建成功响应（带消息）
     * <p>
     * Create success response (with message)
     * </p>
     *
     * @param data 响应数据 / Response data
     * @param message 消息 / Message
     * @param <T> 数据类型 / Data type
     * @return API 响应 / API response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    /**
     * 创建资源创建成功响应
     * <p>
     * Create resource created success response
     * </p>
     * <p>
     * 用于 POST 请求成功创建资源后的响应。
     * </p>
     * <p>
     * Used for response after successful resource creation via POST request.
     * </p>
     *
     * @param data 响应数据 / Response data
     * @param <T> 数据类型 / Data type
     * @return API 响应，消息为 "Created successfully" / API response with "Created successfully" message
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, "Created successfully");
    }
}
