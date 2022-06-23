package com.plantingio.server.Utility;

import java.math.BigDecimal;

public class ServiceResponse {

    private int id;
    private String type;
    private float price;
    private float discount;
    private String photoPath;
    private int nurseryId;
    private String nurseryName;

    public ServiceResponse() {
    }

    public ServiceResponse(int id, String type, float price, float discount, String photoPath, int nurseryId, String nurseryName) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.discount = discount;
        this.photoPath = photoPath;
        this.nurseryId = nurseryId;
        this.nurseryName = nurseryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getNurseryName() {
        return nurseryName;
    }

    public void setNurseryName(String nurseryName) {
        this.nurseryName = nurseryName;
    }

    public int getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(int nurseryId) {
        this.nurseryId = nurseryId;
    }
}
