package vn.bachhoa.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="carts")
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    @OneToOne @JoinColumn(name="user_id", unique = true)
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public Integer getCartId() { return cartId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
