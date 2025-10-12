package vn.bachhoa.promotions;

import java.time.LocalDateTime;
import java.util.List;

public class Promotion {
    private Integer id;
    private String code;
    private String title;
    private String description;
    private String discountType; // "PERCENTAGE" or "FIXED"
    private Double discountValue;
    private Double minOrderAmount;
    private Boolean active;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer createdBy;
    private List<Integer> categoryIds; // categories applied

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

    public Double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(Double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public List<Integer> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Integer> categoryIds) { this.categoryIds = categoryIds; }
}
