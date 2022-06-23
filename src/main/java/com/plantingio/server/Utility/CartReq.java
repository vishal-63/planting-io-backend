package com.plantingio.server.Utility;

public class CartReq {
    private Integer itemId;
    private String type;
    private Integer noOfItems;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId (Integer itemId) {
        this.itemId = itemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems (Integer noOfItems) {
        this.noOfItems = noOfItems;
    }
}
