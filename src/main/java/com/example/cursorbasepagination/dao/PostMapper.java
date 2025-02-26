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
     * Kết quả được sắp xếp theo thứ tự giảm dần của thời gian tạo và ID.
     *
     * @param limit Số lượng bài viết tối đa cần lấy
     * @return Danh sách bài viết trang đầu tiên
     */
    List<Post> findFirstPage(@Param("limit") int limit);

    /**
     * Lấy trang tiếp theo dựa trên cursor (ID và thời gian tạo của bài viết cuối cùng).
     * Kết quả được sắp xếp theo thứ tự giảm dần của thời gian tạo và ID.
     *
     * @param lastId ID của bài viết cuối cùng trong trang hiện tại
     * @param lastCreatedAt Thời gian tạo của bài viết cuối cùng trong trang hiện tại
     * @param limit Số lượng bài viết tối đa cần lấy
     * @return Danh sách bài viết của trang tiếp theo
     */
    List<Post> findNextPage(
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);

    /**
     * Lấy trang trước đó dựa trên cursor (ID và thời gian tạo của bài viết đầu tiên).
     * Kết quả được sắp xếp theo thứ tự tăng dần của thời gian tạo và ID.
     * Lưu ý: Kết quả trả về sẽ cần đảo ngược trước khi sử dụng.
     *
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt Thời gian tạo của bài viết đầu tiên trong trang hiện tại
     * @param limit Số lượng bài viết tối đa cần lấy
     * @return Danh sách bài viết của trang trước đó (theo thứ tự tăng dần)
     */
    List<Post> findPreviousPage(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt,
            @Param("limit") int limit);

    /**
     * Kiểm tra xem có trang trước đó hay không dựa trên cursor hiện tại.
     *
     * @param firstId ID của bài viết đầu tiên trong trang hiện tại
     * @param firstCreatedAt Thời gian tạo của bài viết đầu tiên trong trang hiện tại
     * @return 0 nếu không có trang trước, > 0 nếu có trang trước
     */
    Integer checkHasPrevious(
            @Param("firstId") Long firstId,
            @Param("firstCreatedAt") Date firstCreatedAt);

    /**
     * Lấy danh sách bài viết theo category với cơ chế cursor-based pagination.
     * Kết quả được sắp xếp theo thứ tự giảm dần của thời gian tạo và ID.
     *
     * @param category Tên category cần lọc
     * @param lastId ID của bài viết cuối cùng trong trang hiện tại (null nếu là trang đầu tiên)
     * @param lastCreatedAt Thời gian tạo của bài viết cuối cùng trong trang hiện tại (null nếu là trang đầu tiên)
     * @param limit Số lượng bài viết tối đa cần lấy
     * @return Danh sách bài viết theo category
     */
    List<Post> findByCategory(
            @Param("category") String category,
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);

    /**
     * Lấy danh sách bài viết với nhiều điều kiện lọc kết hợp.
     * Kết quả được sắp xếp theo thứ tự giảm dần của thời gian tạo và ID.
     *
     * @param category Category cần lọc (có thể null)
     * @param userId ID của người dùng cần lọc (có thể null)
     * @param startDate Thời gian bắt đầu khoảng thời gian cần lọc (có thể null)
     * @param endDate Thời gian kết thúc khoảng thời gian cần lọc (có thể null)
     * @param lastId ID của bài viết cuối cùng trong trang hiện tại (null nếu là trang đầu tiên)
     * @param lastCreatedAt Thời gian tạo của bài viết cuối cùng trong trang hiện tại (null nếu là trang đầu tiên)
     * @param limit Số lượng bài viết tối đa cần lấy
     * @return Danh sách bài viết thỏa mãn các điều kiện lọc
     */
    List<Post> findWithFilters(
            @Param("category") String category,
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") Date lastCreatedAt,
            @Param("limit") int limit);
}