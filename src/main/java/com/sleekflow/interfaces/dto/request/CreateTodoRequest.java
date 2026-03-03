package com.sleekflow.interfaces.dto.request;

import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建待办事项请求类
 * <p>
 * Create Todo Request Class
 * </p>
 * <p>
 * 创建新待办事项时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when creating a new todo item.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTodoRequest {

    /**
     * 待办事项名称
     * <p>
     * Todo item name
     * </p>
     * <p>
     * 必填，最多 255 个字符。
     * </p>
     * <p>
     * Required, max 255 characters.
     * </p>
     */
    @NotBlank
    @Size(max = 255)
    private String name;

    /**
     * 待办事项描述
     * <p>
     * Todo item description
     * </p>
     * <p>
     * 可选，支持长文本。
     * </p>
     * <p>
     * Optional, supports long text.
     * </p>
     */
    private String description;

    /**
     * 截止日期
     * <p>
     * Due date
     * </p>
     * <p>
     * 可选。
     * </p>
     * <p>
     * Optional.
     * </p>
     */
    private LocalDateTime dueDate;

    /**
     * 待办事项状态
     * <p>
     * Todo item status
     * </p>
     * <p>
     * 可选。
     * 如果未指定，默认为 NOT_STARTED。
     * </p>
     * <p>
     * Optional.
     * Defaults to NOT_STARTED if not specified.
     * </p>
     */
    private TodoStatus status;

    /**
     * 待办事项优先级
     * <p>
     * Todo item priority
     * </p>
     * <p>
     * 可选。
     * 如果未指定，默认为 MEDIUM。
     * </p>
     * <p>
     * Optional.
     * Defaults to MEDIUM if not specified.
     * </p>
     */
    private Priority priority;

    /**
     * 关联的标签 ID 列表
     * <p>
     * List of associated tag IDs
     * </p>
     * <p>
     * 可选。
     * 用于关联现有标签到新创建的待办事项。
     * </p>
     * <p>
     * Optional.
     * Used to associate existing tags with the newly created todo item.
     * </p>
     */
    private List<String> tagIds;
}
