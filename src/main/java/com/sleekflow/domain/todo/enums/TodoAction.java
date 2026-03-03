package com.sleekflow.domain.todo.enums;

/**
 * 待办事项操作类型枚举
 * <p>
 * Todo Action Type Enumeration
 * </p>
 * <p>
 * 定义待办事项上发生的操作类型，用于活动记录（Activity Feed）审计日志。
 * </p>
 * <p>
 * Defines the types of actions performed on todo items, used for activity feed audit logging.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public enum TodoAction {

    /**
     * 待办事项已创建 - Todo Created
     * <p>
     * 记录新待办事项的创建操作
     * </p>
     * <p>
     * Records the creation of a new todo item
     * </p>
     */
    TODO_CREATED,

    /**
     * 待办事项已更新 - Todo Updated
     * <p>
     * 记录待办事项的任何字段更新操作（名称、描述、状态、优先级、截止日期等）
     * </p>
     * <p>
     * Records any field updates to the todo item (name, description, status, priority, due date, etc.)
     * </p>
     */
    TODO_UPDATED,

    /**
     * 待办事项已删除 - Todo Deleted
     * <p>
     * 记录待办事项的软删除操作
     * </p>
     * <p>
     * Records the soft deletion of the todo item
     * </p>
     */
    TODO_DELETED,

    /**
     * 待办事项已分享 - Todo Shared
     * <p>
     * 记录待办事项被分享给其他用户的操作
     * </p>
     * <p>
     * Records the sharing of the todo item with another user
     * </p>
     */
    TODO_SHARED,

    /**
     * 待办事项已取消分享 - Todo Unshared
     * <p>
     * 记录撤销用户对待办事项访问权限的操作
     * </p>
     * <p>
     * Records the revocation of a user's access to the todo item
     * </p>
     */
    TODO_UNSHARED
}
