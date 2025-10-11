package vn.bachhoa.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Json {
  private static final ObjectMapper M = new ObjectMapper();
  public static void write(HttpServletResponse resp, int status, Object body) throws IOException {
    resp.setStatus(status);
    resp.setContentType("application/json; charset=UTF-8");
    M.writeValue(resp.getOutputStream(), body);
  }
}
