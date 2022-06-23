package com.plantingio.server.Model;

import com.plantingio.server.Utility.StringListConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "product_tbl")
public class Product {

    @Id
    @SequenceGenerator(
            name = "product_sequence",
            sequenceName = "product_sequence",
            initialValue = 30001,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "product_sequence"
    )
    @Column(name = "product_id")
    private Integer id;
    @Column(name = "product_name")
    private String name;
    @Column(name = "product_type")
    private String type;
    @Column(name = "price")
    private BigDecimal price;
    private BigDecimal discount;
    private Integer quantity;
    @Column(name = "product_details")
    private String details;
    @Column(name = "photo_path")
    @Convert(converter = StringListConverter.class)
    private List<String> photoPath;
    @Column(name = "nursery_id")
    private Integer nurseryId;
    @Column(name = "is_active")
    private boolean isActive;

    public Product() {
    }

    public Product(String name, String type, BigDecimal price, BigDecimal discount, Integer quantity, String details, List<String> photoPath, Integer nurseryId, boolean isActive) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
        this.details = details;
        this.photoPath = photoPath;
        this.nurseryId = nurseryId;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<String> getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(List<String> photoPath) {
        this.photoPath = photoPath;
    }

    public Integer getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        this.nurseryId = nurseryId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
