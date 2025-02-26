package com.example.cursorbasepagination.dto.response;

import java.util.List;

public class CursorPageResponse <T>{
    private List<T> items;
    private String nextCursor;
    private String previousCursor;
    private boolean hasNext;
    private boolean hasPrevious;

    public CursorPageResponse(List<T> items, String nextCursor, String previousCursor, boolean hasNext, boolean hasPrevious) {
        this.items = items;
        this.nextCursor = nextCursor;
        this.previousCursor = previousCursor;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor) {
        this.nextCursor = nextCursor;
    }

    public String getPreviousCursor() {
        return previousCursor;
    }

    public void setPreviousCursor(String previousCursor) {
        this.previousCursor = previousCursor;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}