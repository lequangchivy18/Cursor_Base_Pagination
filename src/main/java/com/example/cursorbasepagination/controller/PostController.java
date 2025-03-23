package com.example.cursorbasepagination.controller;

import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.example.cursorbasepagination.entity.Post;
import com.example.cursorbasepagination.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Bộ điều khiển REST cho thực thể Bài viết với phân trang dựa trên con trỏ.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    /**
     * Lấy bài viết với phân trang dựa trên con trỏ đơn giản.
     */
    @GetMapping
    public CursorPageResponse<Post> getPosts(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "NEXT") CursorPageRequest.PaginationDirection direction) {

        CursorPageRequest pageRequest = new CursorPageRequest();
        pageRequest.setCursor(cursor);
        pageRequest.setLimit(limit);
        pageRequest.setDirection(direction);

        return postService.getPosts(pageRequest);
    }

    /**
     * Lấy bài viết với nhiều điều kiện lọc và phân trang dựa trên con trỏ.
     */
    @GetMapping("/filter")
    public CursorPageResponse<Post> getPostsWithFilters(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "NEXT") CursorPageRequest.PaginationDirection direction) {

        CursorPageRequest pageRequest = new CursorPageRequest();
        pageRequest.setCursor(cursor);
        pageRequest.setLimit(limit);
        pageRequest.setDirection(direction);

        return postService.getPostsWithFilters(title, category, userId, startDate, endDate, pageRequest);
    }
}