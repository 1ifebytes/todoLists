package com.sleekflow.infrastructure.security;

/**
 * 用户上下文 - ThreadLocal 工具类
 * <p>
 * User Context - ThreadLocal utility class
 * </p>
 * <p>
 * 使用 ThreadLocal 存储当前认证用户的 ID，避免在 Controller 和 Service 层之间手动传递认证信息。
 * </p>
 * <p>
 * Uses ThreadLocal to store the current authenticated user's ID, avoiding manual passing of
 * authentication information between Controller and Service layers.
 * </p>
 * <p>
 * <b>线程安全（Thread Safety）：</b></p>
 * <p>
 * ThreadLocal 确保每个请求线程都有独立的用户上下文，在同步应用中是线程安全的。
 * </p>
 * <p>
 * ThreadLocal ensures each request thread has its own isolated user context, making it
 * thread-safe in synchronous applications.
 * </p>
 * <p>
 * <b>内存泄漏防护（Memory Leak Prevention）：</b></p>
 * <p>
 * JwtAuthenticationFilter 会在请求结束时调用 clear() 方法清理 ThreadLocal。
 * </p>
 * <p>
 * JwtAuthenticationFilter calls clear() at the end of each request to clean up ThreadLocal.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public final class UserContext {

    /**
     * ThreadLocal 存储当前认证用户的 ID
     * <p>
     * ThreadLocal storage for the current authenticated user's ID
     * </p>
     */
    private static final ThreadLocal<String> CURRENT_USER_ID = new ThreadLocal<>();

    /**
     * 私有构造函数，防止实例化
     * <p>
     * Private constructor to prevent instantiation
     * </p>
     */
    private UserContext() {
    }

    /**
     * 设置当前认证用户的 ID
     * <p>
     * Set current authenticated user ID
     * </p>
     * <p>
     * 由 JwtAuthenticationFilter 在请求开始时调用。
     * </p>
     * <p>
     * Called by JwtAuthenticationFilter at the start of a request.
     * </p>
     *
     * @param userId 用户 ID / User ID
     */
    public static void setCurrentUserId(String userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * 获取当前认证用户的 ID
     * <p>
     * Get current authenticated user ID
     * </p>
     * <p>
     * 由 Service 层方法调用，自动获取当前用户。
     * </p>
     * <p>
     * Called by Service layer methods to automatically obtain the current user.
     * </p>
     *
     * @return 用户 ID / User ID
     * @throws IllegalStateException 如果在未认证的上下文中调用 / if called in an unauthenticated context
     */
    public static String getCurrentUserId() {
        String userId = CURRENT_USER_ID.get();
        if (userId == null) {
            throw new IllegalStateException("No authenticated user found in current context");
        }
        return userId;
    }

    /**
     * 获取当前认证用户的 ID（可选返回）
     * <p>
     * Get current authenticated user ID (optional return)
     * </p>
     * <p>
     * 用于可能不需要认证的端点，返回 null 而不是抛出异常。
     * </p>
     * <p>
     * Used for endpoints that may not require authentication, returns null instead of throwing.
     * </p>
     *
     * @return 用户 ID，如果未认证则返回 null / User ID, or null if not authenticated
     */
    public static String getCurrentUserIdOrNull() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 清除当前线程的用户上下文
     * <p>
     * Clear current thread's user context
     * </p>
     * <p>
     * 必须在请求处理完成后调用，防止 ThreadLocal 内存泄漏。
     * </p>
     * <p>
     * Must be called after request processing completes to prevent ThreadLocal memory leak.
     * </p>
     * <p>
     * 由 JwtAuthenticationFilter 的 finally 块调用。
     * </p>
     * <p>
     * Called by JwtAuthenticationFilter's finally block.
     * </p>
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
    }
}
