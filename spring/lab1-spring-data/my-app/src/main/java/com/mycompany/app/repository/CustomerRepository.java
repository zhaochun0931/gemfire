package com.mycompany.app.repository; // <-- MUST BE EXACTLY THIS

import com.mycompany.app.domain.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
}