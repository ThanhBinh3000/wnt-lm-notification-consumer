package vn.com.gsoft.consumer.util.system;

import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DataUtils {
    public static <T> Page<T> convertPage(Page<Tuple> tuples, Class<T> clazz) {
        Page<T> page = tuples.map(tuple -> {
            try {
                T e = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    var value = tuple.get(field.getName());
                    if (value != null) {
                        value = convertValueIfNeeded(value, field.getType());
                        field.setAccessible(true);
                        field.set(e, value);
                    }
                }
                return e;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        });

        return page;
    }

    public static <T> List<T> convertList(List<Tuple> tuples, Class<T> clazz) {
        List<T> lst = tuples.stream().map(tuple -> {
            try {
                T e = clazz.getDeclaredConstructor().newInstance(); // Sử dụng getDeclaredConstructor().newInstance() để tạo instance
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Cho phép truy cập các thuộc tính
                    Object value = tuple.get(field.getName());
                    if (value != null) {
                        value = convertValueIfNeeded(value, field.getType());
                        field.set(e, value);
                    }
                }
                return e;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }).collect(Collectors.toList());
        return lst;
    }

    public static <T> T convertOne(Tuple tuple, Class<T> clazz) {
        try {
            T e = clazz.getDeclaredConstructor().newInstance(); // Sử dụng getDeclaredConstructor().newInstance() để tạo instance
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Cho phép truy cập các thuộc tính
                Object value = tuple.get(field.getName());
                if (value != null) {
                    value = convertValueIfNeeded(value, field.getType());
                    field.set(e, value);
                }
            }
            return e;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Object convertValueIfNeeded(Object value, Class<?> targetType) {
        if (value.getClass().equals(targetType)) {
            return value;
        } else if (value instanceof Integer && targetType.equals(Long.class)) {
            return ((Integer) value).longValue();
        } else if (value instanceof Float && targetType.equals(Double.class)) {
            return ((Float) value).doubleValue();
        }
        // Thêm các trường hợp chuyển đổi khác nếu cần
        return value;
    }

    public static Date toDate(LocalDateTime localDateTime) {
        // Convert LocalDateTime to ZonedDateTime
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // Convert ZonedDateTime to Date
        return Date.from(zonedDateTime.toInstant());
    }

    public static String toString(Date date) {
        // Create an instance of SimpleDateFormat
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Format the date
        return formatter.format(date);
    }

}