package vn.bachhoa.service;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dto.UserProfileDTO;
import vn.bachhoa.model.User;

public class UserProfileService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Lấy thông tin hồ sơ người dùng dựa trên ID.
     * @param userId ID của người dùng.
     * @return UserProfileDTO chứa thông tin công khai của người dùng, hoặc null nếu không tìm thấy.
     */
    public UserProfileDTO getProfile(int userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return null; // Hoặc ném ra một ngoại lệ tùy chỉnh
        }
        return new UserProfileDTO(user);
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     * @param userId ID của người dùng cần cập nhật.
     * @param profileDTO Đối tượng DTO chứa thông tin mới.
     * @return UserProfileDTO đã được cập nhật.
     * @throws IllegalStateException nếu không tìm thấy người dùng.
     */
    public UserProfileDTO updateProfile(int userId, UserProfileDTO profileDTO) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalStateException("User not found with id: " + userId);
        }

        // Chỉ cập nhật các trường được phép thay đổi
        user.setUsername(profileDTO.getUsername());
        user.setPhoneNumber(profileDTO.getPhoneNumber());
        // Lưu ý: Không cho phép cập nhật email qua đây để đảm bảo an toàn,
        // việc thay đổi email nên có quy trình xác thực riêng.

        userDAO.update(user);
        
        // Trả về DTO mới nhất sau khi cập nhật
        return new UserProfileDTO(user);
    }
}