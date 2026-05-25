package com.mycompany.app;

import com.mycompany.app.config.GemFireDataConfig;
import com.mycompany.app.domain.Customer;
import com.mycompany.app.repository.CustomerRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Bootstrap standard Spring Framework container using our config class
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(GemFireDataConfig.class);

        // Retrieve the repository bean from the context
        CustomerRepository repository = context.getBean(CustomerRepository.class);

        // Interact with Tanzu GemFire 10.1 seamlessly
        repository.save(new Customer(123L, "John Doe"));

        Customer retrieved = repository.findById(123L).orElse(null);
        System.out.println("Retrieved Customer: " + retrieved.getName());

        context.close();
    } // Closes the main method
} // Closes the App class (The extra brace below this was causing the error)