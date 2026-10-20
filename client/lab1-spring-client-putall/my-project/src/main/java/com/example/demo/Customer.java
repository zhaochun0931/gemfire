package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

// 指定该实体映射到名为 Customer 的 Region
@Region("Customer")
public class Customer {
    
    @Id
    private String id;
    private String name;
    private int age;

    public Customer(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', age=" + age + '}';
    }
}
