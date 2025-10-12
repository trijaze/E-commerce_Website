package vn.bachhoa.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;
    private double discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters và Setters
}
