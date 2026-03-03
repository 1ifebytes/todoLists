package com.sleekflow.infrastructure.persistence.team;

import com.sleekflow.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 团队仓储接口
 * <p>
 * Team Repository Interface
 * </p>
 * <p>
 * 提供 Team 实体的数据库访问操作。
 * </p>
 * <p>
 * Provides database access operations for the Team entity.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, String> {

    /**
     * 查找用户所属的所有团队
     * <p>
     * Find all teams where user is a member
     * </p>
     * <p>
     * 通过 team_members 连接表查询用户所属的团队。
     * </p>
     * <p>
     * List teams where the caller is a member (via team_members join).
     * </p>
     *
     * @param userId 用户 ID / User ID
     * @return 团队列表 / List of teams
     */
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.user.id = :userId")
    List<Team> findAllByMemberUserId(@Param("userId") String userId);
}
