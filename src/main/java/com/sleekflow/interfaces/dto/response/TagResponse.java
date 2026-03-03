package com.sleekflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 标签响应类
 * <p>
 * Tag Response Class
 * </p>
 * <p>
 * 标签的基本信息。
 * </p>
 * <p>
 * Basic information of a tag.
 * </p>
 *
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {

    /**
     * 标签 ID
     * <p>
     * Tag ID
     * </p>
     */
    private String id;

    /**
     * 标签名称
     * <p>
     * Tag name
     * </p>
     */
    private String name;
}
