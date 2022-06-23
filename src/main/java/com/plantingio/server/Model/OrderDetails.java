package com.plantingio.server.Model;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "order_details_tbl")
public class OrderDetails {

    @Id
    @SequenceGenerator(
            name = "order_details_sequence",
            sequenceName = "order_details_sequence",
            initialValue = 80005,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "order_details_sequence"
    )
    private Integer orderDetailsId;
    private Integer orderId;
    private Integer productId;
    private Integer serviceId;
    private Integer nurseryId;
    private Integer quantity;
    private BigDecimal price;

    public OrderDetails() {
    }

    public OrderDetails(Integer orderDetailsId, Integer orderId, Integer productId, Integer serviceId, Integer nurseryId, Integer quantity, BigDecimal price) {
        this.orderDetailsId = orderDetailsId;
        this.orderId = orderId;
        this.productId = productId;
        this.serviceId = serviceId;
        this.nurseryId = nurseryId;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        this.nurseryId = nurseryId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(Integer orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }
}
