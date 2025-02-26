package com.example.cursorbasepagination.service;

import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.example.cursorbasepagination.entity.Post;

import java.util.Date;

public interface PostService {
    /**
     * Lấy danh sách bài viết theo cơ chế cursor-based pagination
     * @param pageRequest Thông tin request pagination
     * @return Kết quả phân trang
     */
    CursorPageResponse<Post> getPosts(CursorPageRequest pageRequest);

    /**
     * Lấy danh sách bài viết theo category với phân trang
     * @param category Category cần lọc
     * @param pageRequest Thông tin request pagination
     * @return Kết quả phân trang
     */
    CursorPageResponse<Post> getPostsByCategory(String category, CursorPageRequest pageRequest);

    /**
     * Lấy danh sách bài viết với nhiều điều kiện lọc
     * @param category Category cần lọc (có thể null)
     * @param userId ID của người dùng (có thể null)
     * @param startDate Ngày bắt đầu (có thể null)
     * @param endDate Ngày kết thúc (có thể null)
     * @param pageRequest Thông tin request pagination
     * @return Kết quả phân trang
     */
    CursorPageResponse<Post> getPostsWithFilters(String category, Long userId,
                                                 Date startDate, Date endDate,
                                                 CursorPageRequest pageRequest);
}