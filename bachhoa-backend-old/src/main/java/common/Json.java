package common;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
  private static final ObjectMapper M = new ObjectMapper();
  public static void write(HttpServletResponse resp, int status, Object body) throws IOException {
    resp.setStatus(status);
    resp.setContentType("application/json; charset=UTF-8");
    M.writeValue(resp.getOutputStream(), body);
  }
}
