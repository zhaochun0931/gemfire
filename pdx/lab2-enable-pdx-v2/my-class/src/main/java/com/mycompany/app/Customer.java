package com.mycompany.app;

import java.io.Serializable;

public class Customer implements Serializable {
    private String customerId;
    private String name;
    private int age;
    private String email; // 1. 新增字段

    // 必须保留无参构造器供序列化器反射使用
    public Customer() {}

    // 2. 保留原有的 3 参数构造器，防止破坏既有测试代码
    public Customer(String customerId, String name, int age) {
        this(customerId, name, age, null);
    }

    // 3. 重载包含新字段的全参构造器
    public Customer(String customerId, String name, int age, String email) {
        this.customerId = customerId;
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // 原有 Getter / Setter
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    // 4. 新字段的 Getter / Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}
