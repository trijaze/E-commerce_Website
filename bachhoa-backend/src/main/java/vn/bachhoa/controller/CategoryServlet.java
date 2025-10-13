package vn.bachhoa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import vn.bachhoa.dto.CategoryDTO;
import vn.bachhoa.model.Category;
import vn.bachhoa.dao.CategoryDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/categories")
public class CategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final ObjectMapper mapper;
    private final CategoryDAO categoryDAO;

    public CategoryServlet() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
        this.categoryDAO = new CategoryDAO(); // tạo instance DAO
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json; charset=UTF-8");

        try {
            // gọi method non-static findAll()
            List<Category> categories = categoryDAO.findAll();

            List<CategoryDTO> dtoList = categories.stream()
                    .map(CategoryDTO::new)
                    .collect(Collectors.toList());

            String json = mapper.writeValueAsString(dtoList);
            response.getWriter().write(json);

            System.out.println("✅ [CategoryServlet] Sent categories: " + dtoList.size());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
