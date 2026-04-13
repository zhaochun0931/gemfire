package com.example.my_gemfire_spring_client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;

@SpringBootApplication
// Connects to the GemFire Locator. Adjust "localhost" and "10334" if your server is elsewhere.
@ClientCacheApplication(name = "MySpringClient", locators = {
    @ClientCacheApplication.Locator(host = "172.16.204.139", port = 10334)
})
// Instructs Spring to look for @Region annotations on entities in the same package as MyEntity.class
@EnableEntityDefinedRegions(basePackageClasses = MyEntity.class)
public class MyGemfireSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGemfireSpringClientApplication.class, args);
    }

    @Bean
    public CommandLineRunner insertDataRunner(MyEntityRepository repository, ApplicationContext context) {
        return args -> {
            System.out.println("Starting data insertion into GemFire...");

            // Insert 100 entries into the region
            for (long i = 1; i <= 100; i++) {
                MyEntity entity = new MyEntity(i, "GemFire Data Payload " + i);
                repository.save(entity);
            }

            System.out.println("Successfully inserted 100 entries.");
            System.out.println("Total items currently in 'myRegion': " + repository.count());

            System.out.println("Task complete. Initiating graceful shutdown...");
            
            // Gracefully closes the Spring context and exits the application
            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        };
    }
}
