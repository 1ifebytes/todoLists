package com.sleekflow.interfaces.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 更新团队请求类
 * <p>
 * Update Team Request Class
 * </p>
 * <p>
 * 更新团队信息时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when updating team information.
 * </p>
 * <p>
 * <b>重要说明（Important Note）：</b></p>
 * <p>
 * 所有字段都是可选的 — 仅更新非 null 的字段。
 * </p>
 * <p>
 * All fields are optional — only non-null fields are updated.
 * </p>
 * <p>
 * TeamServiceImpl 中仅更新请求中非 null 的字段。
 * </p>
 * <p>
 * TeamServiceImpl only updates fields that are non-null in this request.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamRequest {

    /**
     * 团队名称
     * <p>
     * Team name
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
