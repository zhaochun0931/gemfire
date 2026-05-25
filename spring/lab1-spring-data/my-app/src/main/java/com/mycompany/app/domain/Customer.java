package com.mycompany.app.domain;

import java.io.Serializable; // 1. Import the serialization library
import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;

@Region("Customers")
public class Customer implements Serializable { // 2. Add the interface here

    // 3. Recommended: Add a unique version ID for serialization stability
    private static final long serialVersionUID = 1L;

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