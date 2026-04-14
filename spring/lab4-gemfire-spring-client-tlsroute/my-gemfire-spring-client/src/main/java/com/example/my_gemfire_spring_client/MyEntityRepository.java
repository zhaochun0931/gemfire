package com.example.my_gemfire_spring_client;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyEntityRepository extends CrudRepository<MyEntity, Long> {
    // Spring Data automatically provides standard CRUD methods like save(), findById(), count(), etc.
}
