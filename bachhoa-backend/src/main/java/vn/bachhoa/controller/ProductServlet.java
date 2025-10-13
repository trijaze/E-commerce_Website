package vn.bachhoa.controller;

import vn.bachhoa.dao.ProductDAO;
import vn.bachhoa.dto.ProductDTO;
import vn.bachhoa.dto.ProductDetailDTO;
import vn.bachhoa.model.Product;
import vn.bachhoa.model.Category;
import vn.bachhoa.model.Supplier;
import vn.bachhoa.util.JsonUtil;
import vn.bachhoa.util.JPAUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/** ✅ Servlet xử lý API sản phẩm: /api/products, /api/products/{id}, /api/products/{id}/related */
public class ProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String path = Optional.ofNullable(req.getPathInfo()).orElse("").trim();

        try {
            if (path.isEmpty() || "/".equals(path)) {
                handleList(req, resp);
                return;
            }

            String[] parts = Arrays.stream(path.split("/"))
                    .filter(s -> s != null && !s.isBlank())
                    .toArray(String[]::new);

            Integer id = tryParseInt(parts[0]);
            if (id == null) {
                writeError(resp, 400, "Invalid product id");
                return;
            }

            if (parts.length == 1) {
                handleDetail(id, resp);
            } else if (parts.length == 2 && "related".equalsIgnoreCase(parts[1])) {
                int limit = tryParseInt(req.getParameter("limit"), 8);
                handleRelated(id, limit, resp);
            } else {
                writeError(resp, 404, "Not found");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            writeError(resp, 500, ex.getMessage());
        }
    }

    /** 🔹 Danh sách sản phẩm hoặc theo categoryId */
    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String catParam = req.getParameter("categoryId");
        List<ProductDTO> list;
        if (catParam != null && !catParam.isBlank()) {
            // Sử dụng findAllDTO rồi lọc theo category
            list = productDAO.findAllDTO();
            int catId = Integer.parseInt(catParam);
            list = list.stream().filter(p -> {
                return p.getCategoryName() != null && p.getCategoryName().equalsIgnoreCase(String.valueOf(catId));
            }).collect(java.util.stream.Collectors.toList());
        } else {
            list = productDAO.findAllDTO();
        }
        JsonUtil.ok(resp, wrap(list));
    }

    /** 🔹 Chi tiết sản phẩm */
    private void handleDetail(Integer id, HttpServletResponse resp) throws IOException {
        ProductDetailDTO dto = productDAO.findDetailDTOById(id);
        if (dto == null) {
            writeError(resp, 404, "Product not found");
            return;
        }
        JsonUtil.ok(resp, wrap(dto));
    }

    /** 🔹 Sản phẩm liên quan */
    private void handleRelated(Integer id, int limit, HttpServletResponse resp) throws IOException {
        List<ProductDTO> list = productDAO.findRelatedDTO(id, limit);
        JsonUtil.ok(resp, wrap(list));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        try {
            // Đọc JSON từ request body
            String jsonStr = req.getReader().lines()
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));

            // Parse JSON thành Map để xử lý categoryId và supplierId
            Map<String, Object> data = JsonUtil.fromJson(jsonStr, Map.class);

            // Tạo Product object và set các trường cơ bản
            Product product = new Product();
            product.setName((String) data.get("name"));
            product.setSku((String) data.get("sku"));
            product.setDescription((String) data.get("description"));

            // Xử lý basePrice
            Object basePriceObj = data.get("basePrice");
            if (basePriceObj != null) {
                BigDecimal basePrice = null;
                if (basePriceObj instanceof Number) {
                    basePrice = BigDecimal.valueOf(((Number) basePriceObj).doubleValue());
                } else if (basePriceObj instanceof String) {
                    basePrice = new BigDecimal((String) basePriceObj);
                }
                product.setBasePrice(basePrice);
            }

            // Xử lý categoryId - tạo Category object
            Object categoryIdObj = data.get("categoryId");
            if (categoryIdObj != null) {
                Integer categoryId = null;
                if (categoryIdObj instanceof Number) {
                    categoryId = ((Number) categoryIdObj).intValue();
                } else if (categoryIdObj instanceof String) {
                    categoryId = Integer.parseInt((String) categoryIdObj);
                }
                if (categoryId != null) {
                    Category category = new Category();
                    category.setCategoryId(categoryId);
                    product.setCategory(category);
                }
            }

            // Xử lý supplierId - tạo Supplier object
            Object supplierIdObj = data.get("supplierId");
            if (supplierIdObj != null) {
                Integer supplierId = null;
                if (supplierIdObj instanceof Number) {
                    supplierId = ((Number) supplierIdObj).intValue();
                } else if (supplierIdObj instanceof String) {
                    supplierId = Integer.parseInt((String) supplierIdObj);
                }
                if (supplierId != null) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(supplierId);
                    product.setSupplier(supplier);
                }
            }

            // Validate dữ liệu cần thiết
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, new ErrorResponse("Product name is required"));
                return;
            }

            if (product.getSku() == null || product.getSku().trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.ok(resp, new ErrorResponse("Product SKU is required"));
                return;
            }

            Product created = productDAO.createProduct(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.ok(resp, new ProductDetailDTO(created));

        } catch (Exception e) {
            e.printStackTrace(); // Debug log
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Create product failed: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Product ID is required"));
            return;
        }

        try {
            int productId = Integer.parseInt(path.substring(1));

            // Đọc dữ liệu update từ request
            String jsonStr = req.getReader().lines()
                    .collect(java.util.stream.Collectors.joining(System.lineSeparator()));

            // Parse JSON thành Map để xử lý từng trường riêng biệt
            Map<String, Object> updateData = JsonUtil.fromJson(jsonStr, Map.class);

            // Sử dụng EntityManager để thực hiện update trong một transaction
            EntityManager em = JPAUtil.getEntityManager();
            try {
                em.getTransaction().begin();

                // Lấy Product với eager loading category và supplier
                String jpql = "SELECT p FROM Product p " +
                             "LEFT JOIN FETCH p.category " +
                             "LEFT JOIN FETCH p.supplier " +
                             "WHERE p.productId = :id";
                Product existing = em.createQuery(jpql, Product.class)
                                   .setParameter("id", productId)
                                   .getSingleResult();

                if (existing == null) {
                    em.getTransaction().rollback();
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonUtil.ok(resp, new ErrorResponse("Product not found"));
                    return;
                }

                // Chỉ update các trường được gửi lên, giữ nguyên các trường khác
                if (updateData.containsKey("name")) {
                    existing.setName((String) updateData.get("name"));
                }

                if (updateData.containsKey("sku")) {
                    existing.setSku((String) updateData.get("sku"));
                }

                if (updateData.containsKey("description")) {
                    existing.setDescription((String) updateData.get("description"));
                }

                if (updateData.containsKey("basePrice")) {
                    Object basePriceObj = updateData.get("basePrice");
                    if (basePriceObj != null) {
                        BigDecimal basePrice = null;
                        if (basePriceObj instanceof Number) {
                            basePrice = BigDecimal.valueOf(((Number) basePriceObj).doubleValue());
                        } else if (basePriceObj instanceof String) {
                            basePrice = new BigDecimal((String) basePriceObj);
                        }
                        existing.setBasePrice(basePrice);
                    }
                }

                // Chỉ update category nếu có categoryId trong request
                if (updateData.containsKey("categoryId")) {
                    Object categoryIdObj = updateData.get("categoryId");
                    if (categoryIdObj != null) {
                        Integer categoryId = null;
                        if (categoryIdObj instanceof Number) {
                            categoryId = ((Number) categoryIdObj).intValue();
                        } else if (categoryIdObj instanceof String) {
                            categoryId = Integer.parseInt((String) categoryIdObj);
                        }
                        if (categoryId != null) {
                            Category category = em.find(Category.class, categoryId);
                            existing.setCategory(category);
                        }
                    }
                }

                // Chỉ update supplier nếu có supplierId trong request
                if (updateData.containsKey("supplierId")) {
                    Object supplierIdObj = updateData.get("supplierId");
                    if (supplierIdObj != null) {
                        Integer supplierId = null;
                        if (supplierIdObj instanceof Number) {
                            supplierId = ((Number) supplierIdObj).intValue();
                        } else if (supplierIdObj instanceof String) {
                            supplierId = Integer.parseInt((String) supplierIdObj);
                        }
                        if (supplierId != null) {
                            Supplier supplier = em.find(Supplier.class, supplierId);
                            existing.setSupplier(supplier);
                        }
                    }
                }

                // Validate dữ liệu cần thiết
                if (existing.getName() == null || existing.getName().trim().isEmpty()) {
                    em.getTransaction().rollback();
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.ok(resp, new ErrorResponse("Product name is required"));
                    return;
                }

                if (existing.getSku() == null || existing.getSku().trim().isEmpty()) {
                    em.getTransaction().rollback();
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.ok(resp, new ErrorResponse("Product SKU is required"));
                    return;
                }

                // Save changes
                Product updated = em.merge(existing);
                em.getTransaction().commit();

                JsonUtil.ok(resp, new ProductDetailDTO(updated));

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            } finally {
                em.close();
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid product ID format"));
        } catch (Exception e) {
            e.printStackTrace(); // Debug log
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Update product failed: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Product ID is required"));
            return;
        }

        try {
            int productId = Integer.parseInt(path.substring(1));

            ProductDetailDTO existing = productDAO.findDetailDTOById(productId);
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.ok(resp, new ErrorResponse("Product not found"));
                return;
            }

            productDAO.deleteProduct(productId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.ok(resp, new ErrorResponse("Invalid product ID format"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.ok(resp, new ErrorResponse("Delete product failed: " + e.getMessage()));
        }
    }

    // Các hàm phụ trợ
    private void writeError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        JsonUtil.ok(resp, new ErrorResponse(msg));
    }

    private Integer tryParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private Integer tryParseInt(String s, int defaultValue) {
        Integer v = tryParseInt(s); return v == null ? defaultValue : v;
    }

    private Object wrap(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("data", data);
        return map;
    }

    static class ErrorResponse {
        public String error;
        ErrorResponse(String msg) { this.error = msg; }
    }
}
