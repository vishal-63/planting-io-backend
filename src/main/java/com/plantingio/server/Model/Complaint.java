package com.plantingio.server.Model;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "complaint_tbl")
public class Complaint {

    @Id
    @SequenceGenerator(
            name = "complaint_sequence",
            sequenceName = "complaint_sequence",
            initialValue = 90001,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "complaint_sequence"
    )
    private Integer complaintId;
    private Integer userId;
    private Integer orderId;
    private String complaintSubject;
    private String complaintDescription;
    private String reply;
    private Date issueDate;
    private boolean isResolved;

    public Complaint() {
    }

    public Complaint(Integer userId, Integer orderId, String complaintSubject, String complaintDescription, String reply, Date issueDate, boolean isResolved) {
        this.userId = userId;
        this.orderId = orderId;
        this.complaintSubject = complaintSubject;
        this.complaintDescription = complaintDescription;
        this.reply = reply;
        this.issueDate = issueDate;
        this.isResolved = isResolved;
    }

    public Integer getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getComplaintSubject() {
        return complaintSubject;
    }

    public void setComplaintSubject(String complaintSubject) {
        this.complaintSubject = complaintSubject;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
