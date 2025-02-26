package com.example.cursorbasepagination.controller;

import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.example.cursorbasepagination.entity.Post;
import com.example.cursorbasepagination.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

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
}