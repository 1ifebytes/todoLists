package com.sleekflow.infrastructure.exception;

/**
 * 重复资源异常
 * <p>
 * Duplicate Resource Exception
 * </p>
 * <p>
 * 当尝试创建已存在的资源时抛出此异常。
 * </p>
 * <p>
 * Thrown when attempting to create a resource that already exists.
 * </p>
 * <p>
 * 由 GlobalExceptionHandler 处理，返回 HTTP 409（冲突）状态码。
 * </p>
 * <p>
 * Handled by GlobalExceptionHandler, returns HTTP 409 (Conflict) status code.
 * </p>
 * <p>
 * <b>使用场景（Use Cases）：</b></p>
 * <ul>
 *   <li>注册时邮箱已存在 / Email already exists during registration</li>
 *   <li>创建同名标签时用户已有该标签 / User already has a tag with the same name</li>
 *   <li>添加团队成员时用户已在团队中 / User already in team when adding member</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * 使用消息构造异常
     * <p>
     * Construct exception with message
     * </p>
     *
     * @param message 错误消息 / Error message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
