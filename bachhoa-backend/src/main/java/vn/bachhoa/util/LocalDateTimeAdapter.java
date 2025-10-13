package vn.bachhoa.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TypeAdapter cho Gson để xử lý việc chuyển đổi (serialize/deserialize)
 * giữa đối tượng java.time.LocalDateTime và chuỗi JSON theo chuẩn ISO-8601.
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Định dạng chuẩn quốc tế, JavaScript có thể hiểu dễ dàng.
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
        // Chuyển đổi LocalDateTime -> String
        return new JsonPrimitive(formatter.format(localDateTime));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        // Chuyển đổi String -> LocalDateTime
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}