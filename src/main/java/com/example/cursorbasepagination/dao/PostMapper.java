package com.example.cursorbasepagination.dao;

import com.example.cursorbasepagination.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PostMapper {

    // Lấy trang đầu tiên
    List<Post> findFirstPage(@Param("limit") int limit);

    // Lấy trang tiếp theo (sắp xếp giảm dần theo createdAt và id)
    List<Post> findNextPage(
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);

    // Lấy trang trước đó (sắp xếp tăng dần theo createdAt và id)
    List<Post> findPreviousPage(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt,
            @Param("limit") int limit);

    // Kiểm tra xem có trang trước không
    Integer checkHasPrevious(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt);
}