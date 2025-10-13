package vn.bachhoa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO chuyên dụng cho việc cập nhật profile từ frontend.
 * Chỉ chứa các field cần thiết để update.
 */
public class UpdateProfileRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private List<AddressDTO> addresses;

    // Constructor mặc định
    public UpdateProfileRequest() {}

    // Getters & Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressDTO> addresses) {
        this.addresses = addresses;
    }

    // Inner class cho Address
    public static class AddressDTO {
        private String label;
        private String addressLine;
        private String city;
        private String country;
        private String postalCode;
        
        // ✅ SỬA LỖI: Dùng @JsonProperty để map đúng tên field từ frontend
        @JsonProperty("isDefault")
        private boolean isDefault;

        public AddressDTO() {}

        // Getters & Setters
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getAddressLine() {
            return addressLine;
        }

        public void setAddressLine(String addressLine) {
            this.addressLine = addressLine;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }
    }
}