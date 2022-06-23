package com.plantingio.server.Model;

import com.plantingio.server.Utility.ServiceResponse;
import com.plantingio.server.Utility.StringListConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "service_tbl")
public class Gardening {

    @Id
    @SequenceGenerator(
            name = "service_sequence",
            sequenceName = "service_sequence",
            initialValue = 40001,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "service_sequence"
    )
    @Column(name = "service_id")
    private Integer id;
    private String type;
    private String details;
    private BigDecimal price;
    private BigDecimal discount;
    @Column(name = "photo_path")
    private String photoPath;
    @Column(name = "nursery_id")
    private Integer nurseryId;
    private boolean is_active;

    public Gardening() {
    }

    public Gardening(String type, String details, BigDecimal price, String photoPath, Integer nurseryId, boolean is_active) {
        this.type = type;
        this.details = details;
        this.price = price;
        this.photoPath = photoPath;
        this.nurseryId = nurseryId;
        this.is_active = is_active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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

    public Integer getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        this.nurseryId = nurseryId;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
