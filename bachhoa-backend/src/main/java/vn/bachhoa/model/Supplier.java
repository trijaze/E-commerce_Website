package vn.bachhoa.model;

import javax.persistence.*;

@Entity @Table(name="suppliers")
public class Supplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supplierId;

    @Column(nullable=false, length=120) private String name;
    @Column(length=120) private String contactName;

    public Integer getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
}
