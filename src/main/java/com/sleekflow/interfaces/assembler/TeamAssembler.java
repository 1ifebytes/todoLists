package com.sleekflow.interfaces.assembler;

import com.sleekflow.domain.team.Team;
import com.sleekflow.interfaces.dto.response.TeamResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 团队实体组装器
 * <p>
 * Team Entity Assembler
 * </p>
 * <p>
 * 使用 MapStruct 将 Team 实体转换为 TeamResponse DTO。
 * </p>
 * <p>
 * Uses MapStruct to convert Team entity to TeamResponse DTO.
 * </p>
 * <p>
 * <b>映射说明（Mapping Notes）：</b></p>
 * <ul>
 *   <li>memberCount 通过 MapStruct 表达式从 team.getMembers().size() 计算 / memberCount computed from team.getMembers().size() via MapStruct expression</li>
 *   <li>myRole 是调用者的有效角色，由服务层确定并在映射后设置 / myRole is caller's effective role, determined by service layer and set after mapping</li>
 *   <li>createdBy 可为空 — UserAssembler 优雅处理 null（返回 null）/ createdBy is nullable — UserAssembler handles null gracefully</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {UserAssembler.class})
public interface TeamAssembler {

    /**
     * 将 Team 实体转换为 TeamResponse DTO
     * <p>
     * Convert Team entity to TeamResponse DTO
     * </p>
     * <p>
     * memberCount 通过表达式计算成员数量。
     * myRole 被忽略，由服务层在映射后设置。
     * </p>
     * <p>
     * memberCount is computed via expression for member count.
     * myRole is ignored, set by service layer after mapping.
     * </p>
     *
     * @param team 团队实体 / Team entity
     * @return 团队响应 / Team response
     */
    @Mapping(target = "memberCount",
            expression = "java(team.getMembers() == null ? 0 : team.getMembers().size())")
    @Mapping(target = "myRole", ignore = true)
    TeamResponse toResponse(Team team);
}
