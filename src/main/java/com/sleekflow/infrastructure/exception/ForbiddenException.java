package com.sleekflow.infrastructure.exception;

/**
 * 禁止访问异常
 * <p>
 * Forbidden Exception
 * </p>
 * <p>
 * 当用户没有权限执行请求的操作时抛出此异常。
 * </p>
 * <p>
 * Thrown when a user does not have permission to perform the requested operation.
 * </p>
 * <p>
 * 由 GlobalExceptionHandler 处理，返回 HTTP 403 状态码。
 * </p>
 * <p>
 * Handled by GlobalExceptionHandler, returns HTTP 403 status code.
 * </p>
 * <p>
 * <b>使用场景（Use Cases）：</b></p>
 * <ul>
     *   <li>非所有者尝试删除待办事项 / Non-owner attempting to delete a todo</li>
     *   <li>VIEWER 尝试编辑待办事项 / VIEWER attempting to edit a todo</li>
     *   <li>非 ADMIN 尝试管理团队成员 / Non-ADMIN attempting to manage team members</li>
     *   <li>用户尝试访问未分享的待办事项 / User attempting to access an unshared todo</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public class ForbiddenException extends RuntimeException {

    /**
     * 使用消息构造异常
     * <p>
     * Construct exception with message
     * </p>
     *
     * @param message 错误消息 / Error message
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
