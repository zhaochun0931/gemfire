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
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.support.ConnectionEndpoint;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

@SpringBootApplication
@ClientCacheApplication(name = "MySpringClient")
@EnableEntityDefinedRegions(basePackageClasses = MyEntity.class)
public class MyGemfireSpringClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGemfireSpringClientApplication.class, args);
    }

    // =========================================================
    // 2. Explicit Pool Configuration (Combines Locator + SNI)
    // =========================================================
    @Bean("gemfirePool")
    public PoolFactoryBean gemfirePool(
            @Value("${gemfire.locator.host}") String locatorHost,
            @Value("${gemfire.locator.port}") int locatorPort,
            @Value("${gemfire.sni.host}") String sniHost,
            @Value("${gemfire.sni.port}") int sniPort) {

        PoolFactoryBean pool = new PoolFactoryBean();

        pool.addLocators(new ConnectionEndpoint(locatorHost, locatorPort));
        pool.setSocketFactory(ProxySocketFactories.sni(sniHost, sniPort));

        return pool;
    }

    // =========================================================
    // 3. SSL Configuration (Fully Externalized)
    // =========================================================
    @Bean
    public ClientCacheConfigurer sslClientCacheConfigurer(
            @Value("${gemfire.ssl.certs-dir}") String certsDir,
            @Value("${gemfire.ssl.storepass}") String storepass,
            @Value("${gemfire.ssl.keystore-file}") String keystoreFile,
            @Value("${gemfire.ssl.truststore-file}") String truststoreFile,
            @Value("${gemfire.ssl.enabled-components}") String enabledComponents,
            @Value("${gemfire.ssl.endpoint-identification-enabled}") String endpointIdEnabled) {

        return (beanName, clientCacheFactoryBean) -> {
            File certsDirectory = new File(certsDir);

            Properties props = new Properties();
            props.setProperty("ssl-enabled-components", enabledComponents);
            props.setProperty("ssl-endpoint-identification-enabled", endpointIdEnabled);
            
            // Build the paths dynamically using the injected file names
            props.setProperty("ssl-keystore", Paths.get(certsDirectory.getAbsolutePath(), keystoreFile).toString());
            props.setProperty("ssl-keystore-password", storepass);
            props.setProperty("ssl-truststore", Paths.get(certsDirectory.getAbsolutePath(), truststoreFile).toString());
            props.setProperty("ssl-truststore-password", storepass);

            clientCacheFactoryBean.setProperties(props);
        };
    }

    // =========================================================
    // 4. Application Runner (Externalized Insert Logic)
    // =========================================================
    @Bean
    public CommandLineRunner insertDataRunner(
            MyEntityRepository repository, 
            ApplicationContext context,
            @Value("${app.runner.insert-count}") int insertCount,
            @Value("${app.runner.payload-prefix}") String payloadPrefix) {
            
        return args -> {
            System.out.println("Starting data insertion via SNI into GemFire...");

            // Use the injected count variable instead of a hardcoded 100
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
