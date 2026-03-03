package com.sleekflow.interfaces.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 创建标签请求类
 * <p>
 * Create Tag Request Class
 * </p>
 * <p>
 * 创建新标签时提交的请求数据。
 * </p>
 * <p>
 * Request data submitted when creating a new tag.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {

    /**
     * 标签名称
     * <p>
     * Tag name
     * </p>
     * <p>
     * 必填，最多 50 个字符。
     * </p>
     * <p>
     * Required, max 50 characters.
     * </p>
     * <p>
     * 标签是用户私有的，不同用户可以拥有相同名称的标签。
     * </p>
     * <p>
     * Tags are user-private: different users can have tags with the same name.
     * </p>
     */
    @NotBlank
    @Size(max = 50)
    private String name;
}
