package com.example.cursorbasepagination.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
}