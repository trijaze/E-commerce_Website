package vn.bachhoa.dto.request;

import java.util.List;

public class OrderRequest {
    public String paymentMethod;
    public List<Item> items;
    public double totalPrice;
    public String promotionCode; // optional

    public static class Item {
        public int productId;
        public int quantity;
        public double price;
    }
}