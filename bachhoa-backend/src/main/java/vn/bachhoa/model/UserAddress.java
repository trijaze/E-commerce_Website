package vn.bachhoa.model;

import javax.persistence.*;

@Entity @Table(name="user_addresses")
public class UserAddress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @Column(length=100) private String label;
    @Column(length=200) private String addressLine;
    @Column(length=80) private String city;
    @Column(length=80) private String country;
    @Column(length=20) private String postalCode;
    private boolean isDefault;

    public Integer getAddressId() { return addressId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
