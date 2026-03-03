package com.sleekflow.interfaces.dto.request;

import com.sleekflow.domain.todo.enums.Priority;
import com.sleekflow.domain.todo.enums.TodoStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新待办事项请求类
 * <p>
 * Update Todo Request Class
 * </p>
 * <p>
 * 更新待办事项时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when updating a todo item.
 * </p>
 * <p>
 * <b>重要说明（Important Note）：</b></p>
 * <p>
 * 所有字段都是可选的 — PUT 端点应用 PATCH 语义。
 * </p>
 * <p>
 * All fields are optional — PUT endpoint applies PATCH semantics.
 * </p>
 * <p>
 * 服务层仅更新请求中非 null 的字段。
 * </p>
 * <p>
 * Service layer only updates fields that are non-null in this request.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTodoRequest {

    /**
     * 待办事项名称
     * <p>
     * Todo item name
     * </p>
     * <p>
     * 可选，最多 255 个字符。
     * </p>
     * <p>
     * Optional, max 255 characters.
     * </p>
     */
    @Size(max = 255)
    private String name;

    /**
     * 待办事项描述
     * <p>
     * Todo item description
     * </p>
     * <p>
     * 可选。
     * </p>
     * <p>
     * Optional.
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
     * </p>
     * <p>
     * Optional.
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
     * </p>
     * <p>
     * Optional.
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
     * 用于更新待办事项的标签关联。
     * </p>
     * <p>
     * Optional.
     * Used to update tag associations for the todo item.
     * </p>
     */
    private List<String> tagIds;
}
