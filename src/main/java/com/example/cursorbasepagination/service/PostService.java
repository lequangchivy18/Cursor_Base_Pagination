package com.example.cursorbasepagination.service;

import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.example.cursorbasepagination.entity.Post;

public interface PostService {
    public CursorPageResponse<Post> getPosts(CursorPageRequest pageRequest);
}
