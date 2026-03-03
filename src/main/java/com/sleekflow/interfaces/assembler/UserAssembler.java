package com.sleekflow.interfaces.assembler;

import com.sleekflow.domain.user.User;
import com.sleekflow.interfaces.dto.response.UserSummaryResponse;
import org.mapstruct.Mapper;

/**
 * 用户实体组装器
 * <p>
 * User Entity Assembler
 * </p>
 * <p>
 * 使用 MapStruct 将 User 实体转换为 UserSummaryResponse DTO。
 * </p>
 * <p>
 * Uses MapStruct to convert User entity to UserSummaryResponse DTO.
 * </p>
 * <p>
 * <b>使用说明（Usage）：</b></p>
 * <p>
 * 被其他 Assembler（TodoAssembler 和 TeamAssembler）通过 @Mapper(uses = {UserAssembler.class}) 重用。
 * </p>
 * <p>
 * Reused by other Assemblers (TodoAssembler and TeamAssembler) via @Mapper(uses = {UserAssembler.class}).
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserAssembler {

    /**
     * 将 User 实体转换为 UserSummaryResponse DTO
     * <p>
     * Convert User entity to UserSummaryResponse DTO
     * </p>
     *
     * @param user 用户实体 / User entity
     * @return 用户信息摘要响应 / User summary response
     */
    UserSummaryResponse toSummary(User user);
}
