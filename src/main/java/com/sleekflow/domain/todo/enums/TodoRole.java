package com.sleekflow.domain.todo.enums;

/**
 * 待办事项角色枚举
 * <p>
 * Todo Role Enumeration
 * </p>
 * <p>
 * 定义用户对待办事项的访问权限级别，用于实现基于角色的访问控制（RBAC）。
 * 所有者（OWNER）由 todos.owner_id 派生，不存储在 todo_permissions 表中。
 * </p>
 * <p>
 * Defines user access permission levels for todo items, used to implement Role-Based Access Control (RBAC).
 * The OWNER role is derived from todos.owner_id and is not stored in the todo_permissions table.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
public enum TodoRole {

    /**
     * 所有者 - Owner
     * <p>
     * 待办事项的创建者和拥有者，拥有全部权限（创建、读取、更新、删除、分享权限）。
     * 该角色通过 todos.owner_id 字段派生，不存储在 todo_permissions 表中。
     * </p>
     * <p>
     * Creator and owner of the todo item with full permissions (create, read, update, delete, share permissions).
     * This role is derived from the todos.owner_id field and is not stored in todo_permissions table.
     * </p>
     */
    OWNER,

    /**
     * 编辑者 - Editor
     * <p>
     * 可以查看和更新待办事项的所有字段，但不能删除或分享权限。
     * </p>
     * <p>
     * Can view and update all fields of the todo item, but cannot delete or share permissions.
     * </p>
     */
    EDITOR,

    /**
     * 查看者 - Viewer
     * <p>
     * 只能查看待办事项，无任何编辑权限。
     * </p>
     * <p>
     * Can only view the todo item, has no editing permissions.
     * </p>
     */
    VIEWER
}
