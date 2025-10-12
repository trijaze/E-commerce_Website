package vn.bachhoa.dto;

import vn.bachhoa.model.User;
import vn.bachhoa.model.UserAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserProfileDTO {
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private List<AddressDTO> addresses;

    // Lớp DTO nội tuyến cho địa chỉ để tránh lộ thông tin không cần thiết
    public static class AddressDTO {
        private String label;
        private String addressLine;
        private String city;
        private String country;
        private String postalCode;
        private boolean isDefault;

        // Getters and Setters
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

    // Constructor để chuyển đổi từ User Entity sang DTO
    public UserProfileDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.createdAt = user.getCreatedAt();
        this.addresses = user.getAddresses().stream()
                .map(UserProfileDTO::mapToAddressDTO)
                .collect(Collectors.toList());
    }
    
    public UserProfileDTO() {} // Cần constructor mặc định cho việc deserialization JSON

    private static AddressDTO mapToAddressDTO(UserAddress address) {
        AddressDTO dto = new AddressDTO();
        dto.setLabel(address.getLabel());
        dto.setAddressLine(address.getAddressLine());
        dto.setCity(address.getCity());
        dto.setCountry(address.getCountry());
        dto.setPostalCode(address.getPostalCode());
        dto.setDefault(address.isDefault());
        return dto;
    }
    
    // Getters and Setters
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