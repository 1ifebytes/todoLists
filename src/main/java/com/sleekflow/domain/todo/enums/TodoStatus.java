package com.sleekflow.domain.todo.enums;

/**
 * 待办事项状态枚举
 * <p>
 * Todo Status Enumeration
 * </p>
 * <p>
 * 定义待办事项的可能状态，用于跟踪任务进度。
 * </p>
 * <p>
 * Defines the possible statuses of a todo item, used to track task progress.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public enum TodoStatus {

    /**
     * 未开始 - Not Started
     * <p>
     * 任务已创建但尚未开始处理
     * </p>
     * <p>
     * Task has been created but not yet started
     * </p>
     */
    NOT_STARTED,

    /**
     * 进行中 - In Progress
     * <p>
     *任务正在处理中
     * </p>
     * <p>
     * Task is currently being worked on
     * </p>
     */
    IN_PROGRESS,

    /**
     * 已完成 - Completed
     * <p>
     * 任务已完成
     * </p>
     * <p>
     * Task has been completed
     * </p>
     */
    COMPLETED
}
