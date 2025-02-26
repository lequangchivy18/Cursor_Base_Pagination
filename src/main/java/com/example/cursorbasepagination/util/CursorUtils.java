package com.example.cursorbasepagination.util;

import com.example.cursorbasepagination.dto.request.CursorPageRequest;
import com.example.cursorbasepagination.dto.response.CursorPageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lớp tiện ích cho phân trang dựa trên cursor.
 * @param <T> Kiểu dữ liệu của entity đang được phân trang
 */
public class CursorUtils<T> {
    // ObjectMapper dùng để chuyển đổi giữa đối tượng Java và JSON
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mã hóa một entity thành chuỗi cursor.
     *
     * @param entity Entity cần mã hóa
     * @param cursorFieldsExtractor Hàm để trích xuất các trường cursor từ entity
     * @return Chuỗi cursor được mã hóa bằng Base64
     */
    public String encodeCursor(T entity, Function<T, Map<String, Object>> cursorFieldsExtractor) {
        try {
            // Nếu entity là null, trả về null
            if (entity == null) {
                return null;
            }

            // Sử dụng hàm trích xuất để lấy các trường cần thiết từ entity
            Map<String, Object> cursorMap = cursorFieldsExtractor.apply(entity);
            // Chuyển đổi map thành chuỗi JSON
            String json = objectMapper.writeValueAsString(cursorMap);
            // Mã hóa chuỗi JSON bằng Base64 và trả về kết quả
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Nếu có lỗi, ném ra RuntimeException với thông báo chi tiết
            throw new RuntimeException("Không thể mã hóa cursor", e);
        }
    }

    /**
     * Giải mã chuỗi cursor thành map các giá trị trường.
     *
     * @param cursor Chuỗi cursor được mã hóa bằng Base64
     * @param valueTransformers Map các hàm chuyển đổi giá trị cho từng trường
     * @return Map các trường cursor với giá trị đã được chuyển đổi
     */
    public Map<String, Object> decodeCursor(String cursor, Map<String, Function<Object, Object>> valueTransformers) {
        try {
            // Nếu cursor là null hoặc rỗng, trả về null
            if (cursor == null || cursor.isEmpty()) {
                return null;
            }

            // Giải mã chuỗi Base64 thành mảng byte
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            // Chuyển đổi mảng byte thành chuỗi JSON
            String json = new String(decodedBytes, StandardCharsets.UTF_8);

            // Chuyển đổi chuỗi JSON thành Map, cần @SuppressWarnings vì việc ép kiểu không an toàn
            @SuppressWarnings("unchecked")
            Map<String, Object> cursorMap = objectMapper.readValue(json, Map.class);

            // Áp dụng các biến đổi giá trị nếu được chỉ định
            if (valueTransformers != null) {
                for (Map.Entry<String, Function<Object, Object>> entry : valueTransformers.entrySet()) {
                    String field = entry.getKey();
                    Function<Object, Object> transformer = entry.getValue();

                    // Chỉ áp dụng biến đổi nếu trường tồn tại và có hàm chuyển đổi
                    if (cursorMap.containsKey(field) && transformer != null) {
                        cursorMap.put(field, transformer.apply(cursorMap.get(field)));
                    }
                }
            }

            return cursorMap;
        } catch (Exception e) {
            // Nếu có lỗi, ném ra RuntimeException với thông báo chi tiết
            throw new RuntimeException("Không thể giải mã cursor", e);
        }
    }

    /**
     * Phiên bản đơn giản của decodeCursor không cần biến đổi giá trị
     */
    public Map<String, Object> decodeCursor(String cursor) {
        // Gọi phương thức decodeCursor với transformer là null
        return decodeCursor(cursor, null);
    }

    /**
     * Phương thức hỗ trợ để tạo các hàm chuyển đổi cho trường kiểu Date
     *
     * @param dateFields Mảng tên các trường cần chuyển đổi thành đối tượng Date
     * @return Map các hàm chuyển đổi cho từng trường
     */
    public static Map<String, Function<Object, Object>> createDateTransformers(String... dateFields) {
        // Khởi tạo map để lưu trữ các hàm chuyển đổi
        Map<String, Function<Object, Object>> transformers = new HashMap<>();

        // Duyệt qua mỗi tên trường và tạo hàm chuyển đổi tương ứng
        for (String field : dateFields) {
            // Thêm hàm chuyển đổi cho trường hiện tại vào map
            transformers.put(field, value -> {
                // Nếu giá trị là kiểu số, chuyển đổi thành Date
                if (value instanceof Number) {
                    return new java.util.Date(((Number) value).longValue());
                }
                // Nếu không, giữ nguyên giá trị
                return value;
            });
        }

        return transformers;
    }

    /**
     * Helper method để xử lý cursor-based pagination với bất kỳ kiểu query nào.
     * Phương thức này giúp tổng quát hóa logic phân trang cho nhiều loại truy vấn khác nhau.
     *
     * @param <T> Kiểu dữ liệu của entity đang được phân trang
     * @param pageRequest Request chứa thông tin phân trang
     * @param firstPageSupplier Supplier để lấy trang đầu tiên
     * @param nextPageSupplier Supplier để lấy trang tiếp theo
     * @param previousPageSupplier Supplier để lấy trang trước đó
     * @param checkHasPreviousSupplier Supplier để kiểm tra có trang trước không
     * @param cursorFieldsExtractor Hàm để trích xuất các trường cursor từ entity
     * @return CursorPageResponse chứa kết quả phân trang
     */
    public static <T> CursorPageResponse<T> handlePagination(
            CursorPageRequest pageRequest,
            Supplier<List<T>> firstPageSupplier,
            Supplier<List<T>> nextPageSupplier,
            Supplier<List<T>> previousPageSupplier,
            Supplier<Integer> checkHasPreviousSupplier,
            Function<T, Map<String, Object>> cursorFieldsExtractor) {

        // Xử lý input
        if (pageRequest.getLimit() == null || pageRequest.getLimit() <= 0) {
            pageRequest.setLimit(10);
        }

        List<T> entities;
        boolean hasNext = false;
        boolean hasPrevious = false;
        CursorUtils<T> cursorUtils = new CursorUtils<>();

        // Tạo transformer cho trường createdAt
        Map<String, Function<Object, Object>> transformers = CursorUtils.createDateTransformers("createdAt");

        // Xử lý trang đầu tiên hoặc trang có cursor
        if (pageRequest.isFirstPage() || pageRequest.getCursor() == null) {
            // Lấy trang đầu tiên
            entities = firstPageSupplier.get();

            // Kiểm tra có trang tiếp theo không
            hasNext = entities.size() > pageRequest.getLimit();

            // Trang đầu tiên không có trang trước
            hasPrevious = false;

            // Loại bỏ phần tử thừa nếu có
            if (hasNext) {
                entities = entities.subList(0, pageRequest.getLimit());
            }
        } else {
            // Giải mã cursor
            Map<String, Object> cursorData = cursorUtils.decodeCursor(pageRequest.getCursor(), transformers);

            if (cursorData != null) {
                if (pageRequest.isNextDirection()) {
                    // Lấy trang tiếp theo
                    entities = nextPageSupplier.get();

                    // Kiểm tra có trang tiếp theo không
                    hasNext = entities.size() > pageRequest.getLimit();

                    // Kiểm tra có trang trước không (sử dụng các trường từ cursor)
                    hasPrevious = true;

                    // Loại bỏ phần tử thừa nếu có
                    if (hasNext) {
                        entities = entities.subList(0, pageRequest.getLimit());
                    }
                } else {
                    // Lấy trang trước đó
                    entities = previousPageSupplier.get();

                    // Kiểm tra có trang trước nữa không
                    hasPrevious = entities.size() > pageRequest.getLimit();

                    // Loại bỏ phần tử thừa nếu có
                    if (hasPrevious) {
                        entities = entities.subList(0, pageRequest.getLimit());
                    }

                    // Kiểm tra có trang tiếp theo không (sử dụng checkHasPrevious)
                    hasNext = true;

                    // Đảo ngược danh sách vì SQL truy vấn theo thứ tự tăng dần
                    Collections.reverse(entities);
                }
            } else {
                // Nếu không thể giải mã cursor, trả về trang đầu tiên
                entities = firstPageSupplier.get();

                // Kiểm tra có trang tiếp theo không
                hasNext = entities.size() > pageRequest.getLimit();

                // Trang đầu tiên không có trang trước
                hasPrevious = false;

                // Loại bỏ phần tử thừa nếu có
                if (hasNext) {
                    entities = entities.subList(0, pageRequest.getLimit());
                }
            }
        }

        // Tạo nextCursor và previousCursor
        String nextCursor = null;
        String previousCursor = null;

        if (!entities.isEmpty()) {
            // Lấy phần tử đầu tiên và cuối cùng
            T firstT = entities.get(0);
            T lastT = entities.get(entities.size() - 1);

            // Tạo nextCursor từ phần tử cuối cùng nếu có trang tiếp theo
            if (hasNext) {
                nextCursor = cursorUtils.encodeCursor(lastT, cursorFieldsExtractor);
            }

            // Tạo previousCursor từ phần tử đầu tiên nếu có trang trước
            if (hasPrevious) {
                previousCursor = cursorUtils.encodeCursor(firstT, cursorFieldsExtractor);
            }
        }

        return new CursorPageResponse<>(entities, nextCursor, previousCursor, hasNext, hasPrevious);
    }
}