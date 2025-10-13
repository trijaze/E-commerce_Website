package vn.bachhoa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeJson(HttpServletResponse resp, Object body, int status) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(status);
        MAPPER.findAndRegisterModules();
        MAPPER.writeValue(resp.getOutputStream(), body);
    }

    public static void ok(HttpServletResponse resp, Object body) throws IOException {
        writeJson(resp, body, HttpServletResponse.SC_OK);
    }
    // Thêm method fromJson để parse JSON string thành object
    public static <T> T fromJson(String jsonStr, Class<T> clazz) throws IOException {
        MAPPER.findAndRegisterModules();
        return MAPPER.readValue(jsonStr, clazz);
    }

    // Thêm method parseJson để parse request body thành Map
    public static Map<String, Object> parseJson(HttpServletRequest req) throws IOException {
        String jsonStr = req.getReader().lines()
                .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
        return fromJson(jsonStr, Map.class);
    }
}
