package com.example.cursorbasepagination.service.impl;

import com.example.cursorbasepagination.dao.PostMapper;
import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.example.cursorbasepagination.entity.Post;
import com.example.cursorbasepagination.service.PostService;
import com.example.cursorbasepagination.util.CursorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    // Định nghĩa hàm extractor để lấy các trường cần thiết từ Post entity
    private Function<Post, Map<String, Object>> cursorFieldsExtractor = post -> {
        Map<String, Object> cursorFields = new HashMap<>();
        cursorFields.put("id", post.getId());
        cursorFields.put("createdAt", post.getCreatedAt());
        return cursorFields;
    };

    @Override
    public CursorPageResponse<Post> getPosts(CursorPageRequest pageRequest) {
        // Xử lý input và tạo limit với +1 để kiểm tra trang tiếp theo
        int limit = (pageRequest.getLimit() != null && pageRequest.getLimit() > 0)
                ? pageRequest.getLimit() + 1 : 11;

        // Giải mã cursor nếu có
        Long cursorId = null;
        Date cursorCreatedAt = null;

        if (pageRequest.getCursor() != null) {
            CursorUtils<Post> cursorUtils = new CursorUtils<>();
            Map<String, Function<Object, Object>> transformers = CursorUtils.createDateTransformers("createdAt");
            Map<String, Object> cursorData = cursorUtils.decodeCursor(pageRequest.getCursor(), transformers);

            if (cursorData != null) {
                cursorId = ((Number) cursorData.get("id")).longValue();
                cursorCreatedAt = (Date) cursorData.get("createdAt");
            }
        }

        // Tạo các suppliers cho từng loại truy vấn
        final Long finalCursorId = cursorId;
        final Date finalCursorCreatedAt = cursorCreatedAt;
        final int finalLimit = limit;

        return CursorUtils.handlePagination(
                pageRequest,
                // First page supplier
                () -> postMapper.findFirstPage(finalLimit),
                // Next page supplier
                () -> postMapper.findNextPage(finalCursorId, finalCursorCreatedAt, finalLimit),
                // Previous page supplier
                () -> postMapper.findPreviousPage(finalCursorId, finalCursorCreatedAt, finalLimit),
                // Check has previous supplier
                () -> postMapper.checkHasPrevious(finalCursorId, finalCursorCreatedAt),
                // Cursor fields extractor
                cursorFieldsExtractor
        );
    }

    @Override
    public CursorPageResponse<Post> getPostsByCategory(String category, CursorPageRequest pageRequest) {
        // Xử lý input và tạo limit với +1 để kiểm tra trang tiếp theo
        int limit = (pageRequest.getLimit() != null && pageRequest.getLimit() > 0)
                ? pageRequest.getLimit() + 1 : 11;

        // Giải mã cursor nếu có
        Long cursorId = null;
        Date cursorCreatedAt = null;

        if (pageRequest.getCursor() != null) {
            CursorUtils<Post> cursorUtils = new CursorUtils<>();
            Map<String, Function<Object, Object>> transformers = CursorUtils.createDateTransformers("createdAt");
            Map<String, Object> cursorData = cursorUtils.decodeCursor(pageRequest.getCursor(), transformers);

            if (cursorData != null) {
                cursorId = ((Number) cursorData.get("id")).longValue();
                cursorCreatedAt = (Date) cursorData.get("createdAt");
            }
        }

        // Tạo các suppliers cho truy vấn by category
        final Long finalCursorId = cursorId;
        final Date finalCursorCreatedAt = cursorCreatedAt;
        final int finalLimit = limit;
        final String finalCategory = category;

        return CursorUtils.handlePagination(
                pageRequest,
                // First page supplier
                () -> postMapper.findByCategoryFirstPage(finalCategory, finalLimit),
                // Next page supplier
                () -> postMapper.findByCategoryNextPage(finalCategory, finalCursorId, finalCursorCreatedAt, finalLimit),
                // Previous page supplier
                () -> postMapper.findByCategoryPreviousPage(finalCategory, finalCursorId, finalCursorCreatedAt, finalLimit),
                // Check has previous supplier
                () -> postMapper.checkHasPreviousCategory(finalCategory, finalCursorId, finalCursorCreatedAt),
                // Cursor fields extractor
                cursorFieldsExtractor
        );
    }

    @Override
    public CursorPageResponse<Post> getPostsWithFilters(String title, String category, Long userId,
                                                        Date startDate, Date endDate, CursorPageRequest pageRequest) {
        // Xử lý input và tạo limit với +1 để kiểm tra trang tiếp theo
        int limit = (pageRequest.getLimit() != null && pageRequest.getLimit() > 0)
                ? pageRequest.getLimit() + 1 : 11;

        // Giải mã cursor nếu có
        Long cursorId = null;
        Date cursorCreatedAt = null;

        if (pageRequest.getCursor() != null) {
            CursorUtils<Post> cursorUtils = new CursorUtils<>();
            Map<String, Function<Object, Object>> transformers = CursorUtils.createDateTransformers("createdAt");
            Map<String, Object> cursorData = cursorUtils.decodeCursor(pageRequest.getCursor(), transformers);

            if (cursorData != null) {
                cursorId = ((Number) cursorData.get("id")).longValue();
                cursorCreatedAt = (Date) cursorData.get("createdAt");
            }
        }

        // Tạo các suppliers cho truy vấn với nhiều điều kiện lọc
        final Long finalCursorId = cursorId;
        final Date finalCursorCreatedAt = cursorCreatedAt;
        final int finalLimit = limit;

        return CursorUtils.handlePagination(
                pageRequest,
                // First page supplier
                () -> postMapper.findWithFiltersFirstPage(title, category, userId, startDate, endDate, finalLimit),
                // Next page supplier
                () -> postMapper.findWithFiltersNextPage(title, category, userId, startDate, endDate,
                        finalCursorId, finalCursorCreatedAt, finalLimit),
                // Previous page supplier
                () -> postMapper.findWithFiltersPreviousPage(title, category, userId, startDate, endDate,
                        finalCursorId, finalCursorCreatedAt, finalLimit),
                // Check has previous supplier
                () -> postMapper.checkHasPreviousFilters(title, category, userId, startDate, endDate,
                        finalCursorId, finalCursorCreatedAt),
                // Cursor fields extractor
                cursorFieldsExtractor
        );
    }
}