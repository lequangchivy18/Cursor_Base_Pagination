package com.example.cursorbasepagination.dto.request;

import java.util.Date;

public class CursorPageRequest {
    private String cursor;
    private Integer limit;
    private Long lastId;
    private Date lastCreatedAt;
    private PaginationDirection direction;

    // Enum để chỉ định hướng điều hướng
    public enum PaginationDirection {
        NEXT, PREVIOUS
    }

    public CursorPageRequest() {
        // Giá trị mặc định
        this.limit = 10;
        this.direction = PaginationDirection.NEXT; // Mặc định là trang tiếp theo
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Date getLastCreatedAt() {
        return lastCreatedAt;
    }

    public void setLastCreatedAt(Date lastCreatedAt) {
        this.lastCreatedAt = lastCreatedAt;
    }

    public PaginationDirection getDirection() {
        return direction;
    }

    public void setDirection(PaginationDirection direction) {
        this.direction = direction;
    }

    // Kiểm tra xem có phải là trang đầu tiên không
    public boolean isFirstPage() {
        return cursor == null || cursor.isEmpty();
    }

    // Kiểm tra nếu đang điều hướng tiếp theo
    public boolean isNextDirection() {
        return direction == PaginationDirection.NEXT;
    }

    // Kiểm tra nếu đang điều hướng trước đó
    public boolean isPreviousDirection() {
        return direction == PaginationDirection.PREVIOUS;
    }
}