package com.plantingio.server.Model;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "payment_tbl")
public class Payment {

    @Id
    @SequenceGenerator(
            name = "payment_sequence",
            sequenceName = "payment_sequence",
            initialValue = 80009,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "payment_sequence"
    )
    private Integer paymentId;
    private Integer orderId;
    private Integer bookingId;
    private Integer nurseryId;
    private BigDecimal amount;
    private BigDecimal commission;
    private String status;

    public Payment() {
    }

    public Payment(Integer orderId, Integer bookingId, Integer nurseryId, BigDecimal amount, BigDecimal commission, String status) {
        this.orderId = orderId;
        this.bookingId = bookingId;
        this.nurseryId = nurseryId;
        this.amount = amount;
        this.commission = commission;
        this.status = status;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getNurseryId() {
        return nurseryId;
    }

    public void setNurseryId(Integer nurseryId) {
        this.nurseryId = nurseryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
