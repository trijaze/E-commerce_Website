package vn.bachhoa.converter;

import vn.bachhoa.model.Order;
import vn.bachhoa.model.OrderItem;
import vn.bachhoa.dto.OrderDTO;
import vn.bachhoa.dto.OrderItemDTO;
import java.util.List;
import java.util.stream.Collectors;

public class OrderConverter {

    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPromotionCode(order.getPromotionCode());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItem> items = order.getItems();
        if(items != null) {
            dto.setItems(items.stream()
                              .map(OrderConverter::toItemDTO)
                              .collect(Collectors.toList()));
        }

        return dto;
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setStatus(item.getStatus());
        // Lưu ý: ko set order trong DTO để tránh vòng lặp
        return dto;
    }
}