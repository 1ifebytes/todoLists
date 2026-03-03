package com.sleekflow.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应包装类
 * <p>
 * Paginated Response Wrapper
 * </p>
 * <p>
 * 包装分页数据的响应格式，包含分页元信息。
 * </p>
 * <p>
 * Response format wrapping paginated data with pagination metadata.
 * </p>
 * <p>
 * <b>响应格式（Response Format）：</b></p>
 * <pre>
 * {
 *   "content": [ ... ],           // 当前页数据 / Current page data
 *   "page": 0,                   // 当前页码（从 0 开始）/ Current page number (zero-based)
 *   "size": 20,                  // 每页大小 / Page size
 *   "totalElements": 100,        // 总元素数 / Total elements
 *   "totalPages": 5,             // 总页数 / Total pages
 *   "last": false                // 是否最后一页 / Is last page
 * }
 * </pre>
 *
 * @param <T> 内容类型 / Content type
 * @author SleekFlow
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 当前页数据列表
     * <p>
     * List of data in current page
     * </p>
     */
    private List<T> content;

    /**
     * 当前页码（从 0 开始）
     * <p>
     * Current page number (zero-based)
     * </p>
     */
    private int page;

    /**
     * 每页大小
     * <p>
     * Page size
     * </p>
     */
    private int size;

    /**
     * 总元素数
     * <p>
     * Total elements across all pages
     * </p>
     */
    private long totalElements;

    /**
     * 总页数
     * <p>
     * Total pages
     * </p>
     */
    private int totalPages;

    /**
     * 是否最后一页
     * <p>
     * Is this the last page
     * </p>
     */
    private boolean last;

    /**
     * 从 Spring Data Page 对象构建分页响应
     * <p>
     * Build paginated response from Spring Data Page object
     * </p>
     *
     * @param springPage Spring Data Page 对象 / Spring Data Page object
     * @param <T> 内容类型 / Content type
     * @return 分页响应 / Paginated response
     */
    public static <T> PageResponse<T> of(Page<T> springPage) {
        return new PageResponse<>(
                springPage.getContent(),           // 内容 / Content
                springPage.getNumber(),            // 页码 / Page number
                springPage.getSize(),              // 页大小 / Page size
                springPage.getTotalElements(),     // 总元素 / Total elements
                springPage.getTotalPages(),        // 总页数 / Total pages
                springPage.isLast()                // 是否最后一页 / Is last page
        );
    }
}
