package com.sleekflow.infrastructure.exception;

/**
 * 资源未找到异常
 * <p>
 * Resource Not Found Exception
 * </p>
 * <p>
 * 当请求的资源不存在时抛出此异常。
 * </p>
 * <p>
 * Thrown when a requested resource is not found.
 * </p>
 * <p>
 * 由 GlobalExceptionHandler 处理，返回 HTTP 404 状态码。
 * </p>
 * <p>
 * Handled by GlobalExceptionHandler, returns HTTP 404 status code.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 使用消息构造异常
     * <p>
     * Construct exception with message
     * </p>
     *
     * @param message 错误消息 / Error message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 使用资源类型和 ID 构造异常
     * <p>
     * Construct exception with resource type and ID
     * </p>
     * <p>
     * 生成消息格式："&lt;resourceType&gt; not found: &lt;id&gt;"
     * </p>
     * <p>
     * Generates message format: "&lt;resourceType&gt; not found: &lt;id&gt;"
     * </p>
     *
     * @param resourceType 资源类型（例如 "Todo"、"User"）/ Resource type (e.g., "Todo", "User")
     * @param id 资源 ID / Resource ID
     */
    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found: " + id);
    }

    /**
     * 使用资源类型、字段名和值构造异常
     * <p>
     * Construct exception with resource type, field name, and value
     * </p>
     * <p>
     * 生成消息格式："&lt;resourceType&gt; not found with &lt;field&gt; = &lt;value&gt;"
     * </p>
     * <p>
     * Generates message format: "&lt;resourceType&gt; not found with &lt;field&gt; = &lt;value&gt;"
     * </p>
     *
     * @param resourceType 资源类型（例如 "User"、"Tag"）/ Resource type (e.g., "User", "Tag")
     * @param field 字段名（例如 "email"、"name"）/ Field name (e.g., "email", "name")
     * @param value 字段值 / Field value
     */
    public ResourceNotFoundException(String resourceType, String field, String value) {
        super(resourceType + " not found with " + field + " = " + value);
    }
}
