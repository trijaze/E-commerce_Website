package vn.bachhoa.service;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dto.UserProfileDTO;
import vn.bachhoa.model.User;
import vn.bachhoa.model.UserAddress;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các logic nghiệp vụ liên quan đến hồ sơ người dùng.
 */
public class UserProfileService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Lấy thông tin hồ sơ của người dùng.
     * @param userId ID của người dùng cần lấy thông tin.
     * @return UserProfileDTO chứa thông tin, hoặc null nếu không tìm thấy.
     */
    public UserProfileDTO getProfile(int userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return null;
        }
        return new UserProfileDTO(user);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng, bao gồm cả địa chỉ.
     * @param userId ID của người dùng đang thực hiện cập nhật.
     * @param profileDTO DTO chứa thông tin mới từ frontend.
     * @return DTO của người dùng sau khi đã cập nhật thành công.
     * @throws IllegalArgumentException nếu dữ liệu mới (username/phone) bị trùng với người dùng khác.
     * @throws IllegalStateException nếu không tìm thấy người dùng gốc trong CSDL.
     */
    public UserProfileDTO updateProfile(int userId, UserProfileDTO profileDTO) {
        User currentUser = userDAO.findById(userId);
        if (currentUser == null) {
            throw new IllegalStateException("Không tìm thấy người dùng với ID: " + userId);
        }

        // --- B1: KIỂM TRA DỮ LIỆU TRÙNG LẶP ---

        // Kiểm tra Username mới
        String newUsername = profileDTO.getUsername();
        if (newUsername != null && !newUsername.trim().isEmpty() && !newUsername.equals(currentUser.getUsername())) {
            User existingUser = userDAO.findByUsername(newUsername);
            if (existingUser != null && !existingUser.getUserId().equals(currentUser.getUserId())) {
                throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng.");
            }
            currentUser.setUsername(newUsername);
        }

        // Kiểm tra Số điện thoại mới
        String newPhoneNumber = profileDTO.getPhoneNumber();
        if (newPhoneNumber != null && !newPhoneNumber.trim().isEmpty() && !newPhoneNumber.equals(currentUser.getPhoneNumber())) {
            User existingUser = userDAO.findByPhoneNumber(newPhoneNumber);
            if (existingUser != null && !existingUser.getUserId().equals(currentUser.getUserId())) {
                throw new IllegalArgumentException("Số điện thoại đã được đăng ký.");
            }
            currentUser.setPhoneNumber(newPhoneNumber);
        }
        
        // --- B2: CẬP NHẬT ĐỊA CHỈ ---
        
        if (profileDTO.getAddresses() != null) {
            // Xóa tất cả địa chỉ cũ để đồng bộ hóa
            currentUser.getAddresses().clear();
            
            // Chuyển đổi từ List<AddressDTO> thành List<UserAddress> và thêm lại
            List<UserAddress> newAddresses = profileDTO.getAddresses().stream()
                .map(dto -> {
                    UserAddress address = new UserAddress();
                    address.setUser(currentUser); // Liên kết địa chỉ với người dùng hiện tại
                    address.setLabel(dto.getLabel());
                    address.setAddressLine(dto.getAddressLine());
                    address.setCity(dto.getCity());
                    address.setCountry(dto.getCountry());
                    address.setPostalCode(dto.getPostalCode());
                    address.setDefault(dto.isDefault());
                    return address;
                }).collect(Collectors.toList());
            
            currentUser.getAddresses().addAll(newAddresses);
        }

        // --- B3: LƯU THAY ĐỔI VÀO DATABASE ---
        userDAO.update(currentUser);
        
        // Trả về DTO mới nhất sau khi cập nhật để frontend đồng bộ
        return new UserProfileDTO(currentUser);
    }
}