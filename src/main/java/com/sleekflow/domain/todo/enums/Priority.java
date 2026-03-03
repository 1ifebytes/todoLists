package com.sleekflow.domain.todo.enums;

/**
 * 待办事项优先级枚举
 * <p>
 * Todo Priority Enumeration
 * </p>
 * <p>
 * 定义待办事项的优先级级别，用于任务排序和重要性标识。
 * </p>
 * <p>
 * Defines the priority levels of todo items, used for task sorting and importance marking.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public enum Priority {

    /**
     * 低优先级 - Low Priority
     * <p>
     * 优先级较低的任务，可以稍后处理
     * </p>
     * <p>
     * Lower priority task, can be handled later
     * </p>
     */
    LOW,

    /**
     * 中优先级 - Medium Priority
     * <p>
     * 优先级中等的任务，需要及时处理
     * </p>
     * <p>
     * Medium priority task, needs timely attention
     * </p>
     */
    MEDIUM,

    /**
     * 高优先级 - High Priority
     * <p>
     * 优先级最高的任务，需要优先处理
     * </p>
     * <p>
     * Highest priority task, needs immediate attention
     * </p>
     */
    HIGH
}
