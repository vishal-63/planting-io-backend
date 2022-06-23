package com.plantingio.server.Model;

import com.plantingio.server.Utility.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cart_tbl")
public class Cart {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "cart_items")
    @Convert(converter = StringListConverter.class)
    private List<String> cartItems;

    @Column(name = "item_type", columnDefinition = "json")
    @Convert(converter = StringListConverter.class)
    private List<String> itemType;

    @Column(name = "no_of_items", columnDefinition = "json")
    @Convert(converter = StringListConverter.class)
    private List<String> noOfItems;

    public Cart() {
    }

    public Cart(Integer userId, List<String> cartItems, List<String> itemType, List<String> noOfItems) {
        this.userId = userId;
        this.cartItems = cartItems;
        this.itemType = itemType;
        this.noOfItems = noOfItems;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<String> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<String> cartItems) {
        this.cartItems = cartItems;
    }

    public List<String> getItemType() {
        return itemType;
    }

    public void setItemType(List<String> itemType) {
        this.itemType = itemType;
    }

    public List<String> getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(List<String> noOfItems) {
        this.noOfItems = noOfItems;
    }
}
