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

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public CursorPageResponse<Post> getPosts(CursorPageRequest pageRequest) {
        // Xử lý input
        if (pageRequest.getLimit() == null || pageRequest.getLimit() <= 0) {
            pageRequest.setLimit(10);
        }

        // Tăng limit thêm 1 để kiểm tra có trang tiếp theo không
        int limit = pageRequest.getLimit() + 1;

        List<Post> posts;
        boolean hasNext = false;
        boolean hasPrevious = false;
        CursorUtils<Post> cursorUtils = new CursorUtils<>();

        // Định nghĩa hàm extractor để lấy các trường cần thiết từ Post entity
        Function<Post, Map<String, Object>> cursorFieldsExtractor = post -> {
            Map<String, Object> cursorFields = new HashMap<>();
            cursorFields.put("id", post.getId());
            cursorFields.put("createdAt", post.getCreatedAt());
            return cursorFields;
        };

        // Tạo transformer cho trường createdAt
        Map<String, Function<Object, Object>> transformers = CursorUtils.createDateTransformers("createdAt");

        // Xử lý trang đầu tiên hoặc trang có cursor
        if (pageRequest.isFirstPage()) {
            // Lấy trang đầu tiên
            posts = postMapper.findFirstPage(limit);

            // Kiểm tra có trang tiếp theo không
            hasNext = posts.size() > pageRequest.getLimit();

            // Trang đầu tiên không có trang trước
            hasPrevious = false;

            // Loại bỏ phần tử thừa nếu có
            if (hasNext) {
                posts = posts.subList(0, pageRequest.getLimit());
            }
        } else {
            // Giải mã cursor
            Map<String, Object> cursorData = cursorUtils.decodeCursor(pageRequest.getCursor(), transformers);

            if (cursorData != null) {
                Long id = ((Number) cursorData.get("id")).longValue();
                Date createdAt = (Date) cursorData.get("createdAt");

                if (pageRequest.isNextDirection()) {
                    // Lấy trang tiếp theo
                    posts = postMapper.findNextPage(id, createdAt, limit);

                    // Kiểm tra có trang tiếp theo không
                    hasNext = posts.size() > pageRequest.getLimit();

                    // Luôn có trang trước khi di chuyển tiếp
                    hasPrevious = true;

                    // Loại bỏ phần tử thừa nếu có
                    if (hasNext) {
                        posts = posts.subList(0, pageRequest.getLimit());
                    }
                } else {
                    // Lấy trang trước đó
                    posts = postMapper.findPreviousPage(id, createdAt, limit);

                    // Kiểm tra có trang trước nữa không
                    hasPrevious = posts.size() > pageRequest.getLimit();

                    // Loại bỏ phần tử thừa nếu có
                    if (hasPrevious) {
                        posts = posts.subList(0, pageRequest.getLimit());
                    }

                    // Luôn có trang tiếp theo khi di chuyển lùi
                    hasNext = true;

                    // Đảo ngược danh sách vì SQL truy vấn theo thứ tự tăng dần
                    Collections.reverse(posts);
                }
            } else {
                // Nếu không thể giải mã cursor, trả về trang đầu tiên
                posts = postMapper.findFirstPage(limit);

                // Kiểm tra có trang tiếp theo không
                hasNext = posts.size() > pageRequest.getLimit();

                // Trang đầu tiên không có trang trước
                hasPrevious = false;

                // Loại bỏ phần tử thừa nếu có
                if (hasNext) {
                    posts = posts.subList(0, pageRequest.getLimit());
                }
            }
        }

        // Tạo nextCursor và previousCursor
        String nextCursor = null;
        String previousCursor = null;

        if (!posts.isEmpty()) {
            // Lấy phần tử đầu tiên và cuối cùng
            Post firstPost = posts.get(0);
            Post lastPost = posts.get(posts.size() - 1);

            // Tạo nextCursor từ phần tử cuối cùng nếu có trang tiếp theo
            if (hasNext) {
                nextCursor = cursorUtils.encodeCursor(lastPost, cursorFieldsExtractor);
            }

            // Tạo previousCursor từ phần tử đầu tiên nếu có trang trước
            if (hasPrevious) {
                previousCursor = cursorUtils.encodeCursor(firstPost, cursorFieldsExtractor);
            }
        }

        return new CursorPageResponse<>(posts, nextCursor, previousCursor, hasNext, hasPrevious);
    }
}