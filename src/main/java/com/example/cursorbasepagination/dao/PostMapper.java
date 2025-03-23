package com.example.cursorbasepagination.dao;

import com.example.cursorbasepagination.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Data Access Object cho entity Post.
 * Cung cấp các phương thức truy vấn dữ liệu bài viết với cơ chế cursor-based pagination.
 */
@Mapper
public interface PostMapper {

    /**
     * Lấy trang đầu tiên của danh sách bài viết.
     *
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết trang đầu tiên
     */
    List<Post> findFirstPage(@Param("limit") int limit);

    /**
     * Lấy trang tiếp theo dựa trên cursor.
     *
     * @param lastId ID của bài viết cuối cùng trong trang hiện tại
     * @param lastCreatedAt ngày tạo của bài viết cuối cùng trong trang hiện tại
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết của trang tiếp theo
     */
    List<Post> findNextPage(
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);

    /**
     * Lấy trang trước đó dựa trên cursor.
     *
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt ngày tạo của bài viết đầu tiên trong trang hiện tại
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết của trang trước đó
     */
    List<Post> findPreviousPage(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt,
            @Param("limit") int limit);

    /**
     * Kiểm tra xem có trang trước đó hay không.
     *
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt ngày tạo của bài viết đầu tiên trong trang hiện tại
     * @return 1 nếu có trang trước, 0 nếu không có
     */
    Integer checkHasPrevious(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt);


    /**
     * Lấy trang đầu tiên với nhiều điều kiện lọc.
     *
     * @param title tiêu đề cần lọc (có thể null)
     * @param category danh mục cần lọc (có thể null)
     * @param userId ID người dùng cần lọc (có thể null)
     * @param startDate ngày bắt đầu của khoảng thời gian cần lọc (có thể null)
     * @param endDate ngày kết thúc của khoảng thời gian cần lọc (có thể null)
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết trang đầu tiên với các điều kiện lọc
     */
    List<Post> findWithFiltersFirstPage(
            @Param("title") String title,
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("limit") int limit);

    /**
     * Lấy trang tiếp theo với nhiều điều kiện lọc.
     *
     * @param title tiêu đề cần lọc (có thể null)
     * @param category danh mục cần lọc (có thể null)
     * @param userId ID người dùng cần lọc (có thể null)
     * @param startDate ngày bắt đầu của khoảng thời gian cần lọc (có thể null)
     * @param endDate ngày kết thúc của khoảng thời gian cần lọc (có thể null)
     * @param lastId ID của bài viết cuối cùng trong trang hiện tại
     * @param lastCreatedAt ngày tạo của bài viết cuối cùng trong trang hiện tại
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết của trang tiếp theo với các điều kiện lọc
     */
    List<Post> findWithFiltersNextPage(
            @Param("title") String title,
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);

    /**
     * Lấy trang trước đó với nhiều điều kiện lọc.
     *
     * @param title tiêu đề cần lọc (có thể null)
     * @param category danh mục cần lọc (có thể null)
     * @param userId ID người dùng cần lọc (có thể null)
     * @param startDate ngày bắt đầu của khoảng thời gian cần lọc (có thể null)
     * @param endDate ngày kết thúc của khoảng thời gian cần lọc (có thể null)
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt ngày tạo của bài viết đầu tiên trong trang hiện tại
     * @param limit số lượng bài viết tối đa trả về
     * @return danh sách bài viết của trang trước đó với các điều kiện lọc
     */
    List<Post> findWithFiltersPreviousPage(
            @Param("title") String title,
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt,
            @Param("limit") int limit);

    /**
     * Kiểm tra xem có trang trước đó không với các điều kiện lọc.
     *
     * @param title tiêu đề cần lọc (có thể null)
     * @param category danh mục cần lọc (có thể null)
     * @param userId ID người dùng cần lọc (có thể null)
     * @param startDate ngày bắt đầu của khoảng thời gian cần lọc (có thể null)
     * @param endDate ngày kết thúc của khoảng thời gian cần lọc (có thể null)
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt ngày tạo của bài viết đầu tiên trong trang hiện tại
     * @return 1 nếu có trang trước, 0 nếu không có
     */
    Integer checkHasPreviousFilters(
            @Param("title") String title,
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt);
}