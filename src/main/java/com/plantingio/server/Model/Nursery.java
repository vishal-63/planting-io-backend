package com.plantingio.server.Model;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "nursery_tbl")
public class Nursery {

    @Id
    @SequenceGenerator(
            name = "nursery_sequence",
            sequenceName = "nursery_sequence",
            initialValue = 20001,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "nursery_sequence"
    )
    @Column(name = "nursery_id")
    private int id;

    @Column(name = "nursery_name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_no")
    private long phone;
    private String address;
    private String city;
    private int pincode;
    private String state;
    private String country;
    private String password;
    @Column(name = "verification_doc_type")
    private String docType;
    @Column(name = "verification_doc_path")
    private String docPath;
    private boolean is_verified;
    private boolean is_active;

    public Nursery() {
    }

    public Nursery(String name, String email, long phone, String address, String city, int pincode, String state, String country, String password, String docType, String docPath, boolean is_verified, boolean is_active) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.country = country;
        this.password = password;
        this.docType = docType;
        this.docPath = docPath;
        this.is_verified = is_verified;
        this.is_active = is_active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public boolean isIs_verfied() {
        return is_verified;
    }

    public void setIs_verfied(boolean is_verfied) {
        this.is_verified = is_verfied;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
