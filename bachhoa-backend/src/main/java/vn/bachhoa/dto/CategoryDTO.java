package vn.bachhoa.dto;

import vn.bachhoa.model.Category;

public class CategoryDTO {
    private int categoryId;
    private String name;

    public CategoryDTO() {}

    public CategoryDTO(Category c) {
        this.categoryId = c.getCategoryId();
        this.name = c.getName();
        // Không gọi c.getSubCategories() hoặc các collection LAZY khác
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }
}
