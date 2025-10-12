package vn.bachhoa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
