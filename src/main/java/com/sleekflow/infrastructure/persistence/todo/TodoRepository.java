package com.sleekflow.infrastructure.persistence.todo;

import com.sleekflow.domain.todo.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 待办事项仓储接口
 * <p>
 * Todo Repository Interface
 * </p>
 * <p>
 * 提供 Todo 实体的数据库访问操作，支持动态查询。
 * </p>
 * <p>
 * Provides database access operations for the Todo entity, supporting dynamic queries.
 * </p>
 * <p>
 * 继承 JpaSpecificationExecutor 以支持 JPA Specification 动态过滤（委托给 TodoFilterSpec）。
 * </p>
 * <p>
 * Extends JpaSpecificationExecutor to support JPA Specification dynamic filtering (delegated to TodoFilterSpec).
 * </p>
 * <p>
 * <b>软删除说明（Soft Delete Note）：</b></p>
 * <p>
 * Todo 实体上的 @SQLRestriction 确保软删除的行永远不会被返回。
 * </p>
 * <p>
 * @SQLRestriction on Todo entity ensures soft-deleted rows are never returned.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, String>, JpaSpecificationExecutor<Todo> {
    // 动态过滤委托给 TodoFilterSpec（JPA Specification）/ Dynamic filtering delegated to TodoFilterSpec
}
