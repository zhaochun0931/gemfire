package com.example.my_gemfire_spring_client;

import org.apache.geode.cache.client.proxy.ProxySocketFactories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

@SpringBootApplication
// 1. REMOVE locators from this annotation. We will configure them explicitly below.
@ClientCacheApplication(name = "MySpringClient")
@EnableEntityDefinedRegions(basePackageClasses = MyEntity.class)
public class MyGemfireSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGemfireSpringClientApplication.class, args);
    }

    // =========================================================
    // 2. Explicit Pool Configuration (Combines Locator + SNI)
    // =========================================================
    // By naming this "gemfirePool", Spring Data GemFire will use it as the default pool.
    @Bean("gemfirePool")
    public PoolFactoryBean gemfirePool() {
        PoolFactoryBean pool = new PoolFactoryBean();

        // Add the internal Kubernetes locator
        pool.addLocators(new ConnectionEndpoint("gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local", 10334));

        // Attach the SNI Socket Factory directly to this pool before it initializes
        pool.setSocketFactory(ProxySocketFactories.sni("localhost", 9000));

        return pool;
    }

    // =========================================================
    // 3. SSL Configuration (Unchanged)
    // =========================================================
    @Bean
    public ClientCacheConfigurer sslClientCacheConfigurer() {
        return (beanName, clientCacheFactoryBean) -> {
            File certsDirectory = new File("./certs");
            String storepass = "unreDY6sNR08-IHBQcw0BADP_leu888D-R-12Lhh1LY=";

            Properties props = new Properties();
            props.setProperty("ssl-enabled-components", "all");
            props.setProperty("ssl-endpoint-identification-enabled", "true");
            props.setProperty("ssl-keystore", Paths.get(certsDirectory.getAbsolutePath(), "keystore.p12").toString());
            props.setProperty("ssl-keystore-password", storepass);
            props.setProperty("ssl-truststore", Paths.get(certsDirectory.getAbsolutePath(), "truststore.p12").toString());
            props.setProperty("ssl-truststore-password", storepass);

            clientCacheFactoryBean.setProperties(props);
        };
    }

    // =========================================================
    // 4. Application Runner (Insert Data and Exit)
    // =========================================================
    @Bean
    public CommandLineRunner insertDataRunner(MyEntityRepository repository, ApplicationContext context) {
        return args -> {
            System.out.println("Starting data insertion via SNI into GemFire...");

            for (long i = 1; i <= 100; i++) {
                MyEntity entity = new MyEntity(i, "GemFire Data Payload " + i);
                repository.save(entity);
            }

            System.out.println("Successfully inserted 100 entries.");
            System.out.println("Total items currently in 'myRegion': " + repository.count());
            System.out.println("Task complete. Initiating graceful shutdown...");

            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        };
    }
}