package com.sleekflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户信息摘要响应类
 * <p>
 * User Summary Response Class
 * </p>
 * <p>
 * 跨域使用的用户信息摘要，包含基本的用户标识信息。
 * </p>
 * <p>
 * Cross-domain user summary containing basic user identification information.
 * </p>
 * <p>
 * <b>使用场景（Usage）：</b></p>
 * <ul>
 *   <li>TodoResponse.owner - 待办事项所有者 / Todo owner</li>
 *   <li>TodoPermissionResponse.user/grantedBy - 权限相关用户 / Permission-related users</li>
 *   <li>ActivityFeedResponse.actor - 活动操作者 / Activity actor</li>
 *   <li>TeamResponse.createdBy - 团队创建者 / Team creator</li>
 *   <li>AuthResponse.user - 认证用户信息 / Authenticated user info</li>
 * </ul>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    /**
     * 用户 ID
     * <p>
     * User ID
     * </p>
     */
    private String id;

    /**
     * 用户名
     * <p>
     * Username
     * </p>
     */
    private String username;

    /**
     * 用户邮箱
     * <p>
     * User email
     * </p>
     */
    private String email;
}
