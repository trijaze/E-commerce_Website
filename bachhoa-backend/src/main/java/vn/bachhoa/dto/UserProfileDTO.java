package vn.bachhoa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.bachhoa.model.User;
import vn.bachhoa.model.UserAddress;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO (Data Transfer Object) cho việc hiển thị và cập nhật hồ sơ người dùng.
 * Bao gồm thông tin cơ bản và danh sách địa chỉ.
 */
public class UserProfileDTO {

    // Các trường đầy đủ để khớp với state `User` của frontend
    private Integer userId;
    private String role;
    private String username;
    private String email;
    private String phoneNumber;

    // Định dạng ngày tháng chuẩn ISO 8601 để JavaScript đọc được
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private List<AddressDTO> addresses;

    /**
     * Constructor mặc định cần thiết cho việc deserialize JSON (chuyển JSON thành Object).
     */
    public UserProfileDTO() {}

    /**
     * Constructor để chuyển đổi từ Entity `User` sang `UserProfileDTO`.
     * @param user Đối tượng entity User từ database.
     */
    public UserProfileDTO(User user) {
        this.userId = user.getUserId();
        this.role = user.getRole();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
        if (user.getAddresses() != null) {
            this.addresses = user.getAddresses().stream()
                    .map(AddressDTO::new) // Sử dụng constructor của AddressDTO
                    .collect(Collectors.toList());
        }
    }

    // --- Lớp DTO nội tuyến cho địa chỉ ---
    public static class AddressDTO {
        private String label;
        private String addressLine;
        private String city;
        private String country;
        private String postalCode;
        private boolean isDefault;

        public AddressDTO() {}

        public AddressDTO(UserAddress address) {
            this.label = address.getLabel();
            this.addressLine = address.getAddressLine();
            this.city = address.getCity();
            this.country = address.getCountry();
            this.postalCode = address.getPostalCode();
            this.isDefault = address.isDefault();
        }

        // Getters & Setters cho AddressDTO
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

    // --- Getters & Setters cho UserProfileDTO ---
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<AddressDTO> getAddresses() { return addresses; }
    public void setAddresses(List<AddressDTO> addresses) { this.addresses = addresses; }
}