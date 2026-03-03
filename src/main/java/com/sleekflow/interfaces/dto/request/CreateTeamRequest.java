package com.sleekflow.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 创建团队请求类
 * <p>
 * Create Team Request Class
 * </p>
 * <p>
 * 创建新团队时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when creating a new team.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamRequest {

    /**
     * 团队名称
     * <p>
     * Team name
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
     * 团队描述
     * <p>
     * Team description
     * </p>
     * <p>
     * 可选，支持长文本。
     * </p>
     * <p>
     * Optional, supports long text.
     * </p>
     */
    private String description;
}
