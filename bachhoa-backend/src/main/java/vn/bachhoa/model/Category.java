package vn.bachhoa.model;

import javax.persistence.*;
import java.util.List;

@Entity @Table(name="categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @ManyToOne @JoinColumn(name="parent_id")
    private Category parent;

    @Column(nullable=false, length=120) private String name;
    @Column(length=255) private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Integer getCategoryId() { return categoryId; }
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
