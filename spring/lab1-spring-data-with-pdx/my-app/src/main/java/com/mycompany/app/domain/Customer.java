package com.mycompany.app.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Region("Customers")
public class Customer { // Cleaned up: No more "implements Serializable"

    @Id
    private Long id;
    private String name;

    public Customer() {}

    public Customer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}