package com.example.my_gemfire_spring_client;

import org.apache.geode.cache.client.proxy.ProxySocketFactories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

@SpringBootApplication
@ClientCacheApplication(name = "MySpringClient")
@EnableEntityDefinedRegions(basePackageClasses = MyEntity.class)
public class MyGemfireSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGemfireSpringClientApplication.class, args);
    }

    // =========================================================
    // 1. Explicit Pool Configuration (Guarantees SNI is attached)
    // =========================================================
    // By naming this "gemfirePool", Spring Data GemFire uses it as the default pool.
    @Bean("gemfirePool")
    public PoolFactoryBean gemfirePool(
            @Value("${gemfire.sni.host}") String sniHost,
            @Value("${gemfire.sni.port}") int sniPort) {

        PoolFactoryBean pool = new PoolFactoryBean();

        // 1. Define the internal Kubernetes locator target
        pool.addLocators(new ConnectionEndpoint("gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local", 10334));
        
        // 2. Attach the SNI proxy to route traffic via localhost:9000
        pool.setSocketFactory(ProxySocketFactories.sni(sniHost, sniPort));

        return pool;
    }

    // =========================================================
    // 2. Application Runner (Externalized Insert Logic)
    // =========================================================
    @Bean
    public CommandLineRunner insertDataRunner(
            MyEntityRepository repository, 
            ApplicationContext context,
            @Value("${app.runner.insert-count}") int insertCount,
            @Value("${app.runner.payload-prefix}") String payloadPrefix) {
            
        return args -> {
            System.out.println("Starting data insertion via SNI into GemFire...");

            for (long i = 1; i <= insertCount; i++) {
                MyEntity entity = new MyEntity(i, payloadPrefix + i);
                repository.save(entity);
            }

            System.out.println("Successfully inserted " + insertCount + " entries.");
            System.out.println("Total items currently in 'myRegion': " + repository.count());
            System.out.println("Task complete. Initiating graceful shutdown...");

            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        };
    }
}
