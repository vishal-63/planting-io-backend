package com.plantingio.server.Model;

import com.plantingio.server.Utility.StringListConverter;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.GenerationType.*;

@Entity
@Table(name = "test")
public class Test {

    @Id
    @GeneratedValue(strategy = AUTO)
    private int id;

    @Convert(converter = StringListConverter.class)
    private List<String> urls;

    public Test() {
    }

    public Test(int id, List<String> urls) {
        this.id = id;
        this.urls = urls;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }


}
