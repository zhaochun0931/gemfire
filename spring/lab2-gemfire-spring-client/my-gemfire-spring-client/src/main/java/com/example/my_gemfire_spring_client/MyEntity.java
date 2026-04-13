package com.example.my_gemfire_spring_client;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Region("myRegion")
public class MyEntity {

    @Id
    private Long id;
    private String data;

    // Default constructor is often required by Spring Data for instantiation
    public MyEntity() {
    }

    public MyEntity(Long id, String data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getData() { 
        return data; 
    }
    
    public void setData(String data) { 
        this.data = data; 
    }

    @Override
    public String toString() {
        return "MyEntity{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
    }
}
