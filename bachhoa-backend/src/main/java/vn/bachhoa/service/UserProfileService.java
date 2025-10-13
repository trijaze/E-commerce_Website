package vn.bachhoa.service;

import vn.bachhoa.dao.UserDAO;
import vn.bachhoa.dto.UserProfileDTO;
import vn.bachhoa.dto.request.UpdateProfileRequest;
import vn.bachhoa.model.User;
import vn.bachhoa.model.UserAddress;
import vn.bachhoa.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class UserProfileService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Lấy thông tin profile của user
     */
    public UserProfileDTO getProfile(Integer userId) {
        User user = userDAO.findById(userId);
        if (user == null) return null;
        return new UserProfileDTO(user);
    }

    /**
     * Cập nhật profile - nhận UpdateProfileRequest thay vì UserProfileDTO
     */
    public UserProfileDTO updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userDAO.findById(userId);
        
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // ✅ Kiểm tra username trùng lặp (nếu có thay đổi)
        if (request.getUsername() != null && !request.getUsername().isEmpty() 
                && !request.getUsername().equals(user.getUsername())) {
            User existingUser = userDAO.findByUsername(request.getUsername());
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Tên đăng nhập đã được sử dụng");
            }
            user.setUsername(request.getUsername());
        }

        // ✅ Kiểm tra phone trùng lặp (nếu có thay đổi)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty() 
                && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            User existingUser = userDAO.findByPhoneNumber(request.getPhoneNumber());
            if (existingUser != null && !existingUser.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // ✅ Xóa toàn bộ địa chỉ cũ
            em.createQuery("DELETE FROM UserAddress WHERE user.userId = :uid")
                .setParameter("uid", userId)
                .executeUpdate();

            // ✅ Thêm địa chỉ mới
            List<UserAddress> newAddresses = new ArrayList<>();
            if (request.getAddresses() != null && !request.getAddresses().isEmpty()) {
                for (UpdateProfileRequest.AddressDTO addrDto : request.getAddresses()) {
                    UserAddress address = new UserAddress();
                    address.setUser(user);
                    address.setLabel(addrDto.getLabel());
                    address.setAddressLine(addrDto.getAddressLine());
                    address.setCity(addrDto.getCity());
                    address.setCountry(addrDto.getCountry());
                    address.setPostalCode(addrDto.getPostalCode());
                    address.setDefault(addrDto.isDefault());
                    
                    em.persist(address);
                    newAddresses.add(address);
                }
            }

            // Gán lại danh sách địa chỉ mới
            user.setAddresses(newAddresses);
            
            // Merge user entity
            user = em.merge(user);
            
            em.getTransaction().commit();

            // ✅ Refresh để lấy dữ liệu mới nhất từ DB
            em.refresh(user);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to update profile: " + e.getMessage(), e);
        } finally {
            em.close();
        }

        // ✅ Trả về DTO mới
        return new UserProfileDTO(user);
    }
}