package org.example;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private int age;

    // Default constructor is required for PDX
    public User() {}

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + "}";
    }
}
