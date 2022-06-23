package com.plantingio.server.Model;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "feedback_tbl")
public class Feedback {

    @Id
    @SequenceGenerator(
            name = "feedback_sequence",
            sequenceName = "feedback_sequence",
            initialValue = 60001,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "feedback_sequence"
    )
    private Integer feedbackId;
    private Integer productId;
    private Integer serviceId;
    private Integer userId;
    private Integer rating;
    private String feedbackDescription;

    public Feedback() {
    }

    public Feedback(Integer productId, Integer serviceId, Integer userId, Integer rating, String feedbackDescription) {
        this.productId = productId;
        this.serviceId = serviceId;
        this.userId = userId;
        this.rating = rating;
        this.feedbackDescription = feedbackDescription;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedbackDescription() {
        return feedbackDescription;
    }

    public void setFeedbackDescription(String feedbackDescription) {
        this.feedbackDescription = feedbackDescription;
    }
}
