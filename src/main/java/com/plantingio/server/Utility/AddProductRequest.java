package com.plantingio.server.Utility;

import java.math.BigDecimal;

public class AddProductRequest {
    private String name;
    private String type;
    private String details;
    private BigDecimal price;
    private BigDecimal discount;
    private Integer quantity;
    private Integer nurseryId;
    private String jwt;

    public Integer getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        this.nurseryId = nurseryId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
