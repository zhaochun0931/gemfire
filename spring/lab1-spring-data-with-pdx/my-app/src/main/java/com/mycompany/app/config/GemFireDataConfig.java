package com.mycompany.app.config;

import java.util.Properties;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@Configuration
@ClientCacheApplication(
        name = "StandaloneGemFireClient",
        locators = { @ClientCacheApplication.Locator(host = "localhost", port = 10334) }
)
@EnableEntityDefinedRegions(basePackages = "com.mycompany.app.domain")
@EnableGemfireRepositories(basePackages = "com.mycompany.app.repository")
public class GemFireDataConfig {

    // FIX: Programmatically register PDX serialization directly onto the ClientCache
    @Bean
    public ClientCacheConfigurer clientCachePdxConfigurer() {
        return (beanName, clientCacheFactoryBean) -> {
            clientCacheFactoryBean.setPdxSerializer(
                    new ReflectionBasedAutoSerializer("com.mycompany.app.domain.*")
            );
            clientCacheFactoryBean.setPdxReadSerialized(false);
        };
    }

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("mcast-port", "0");
        return gemfireProperties;
    }

    @Bean(name = "Customers")
    public ClientRegionFactoryBean<Long, com.mycompany.app.domain.Customer> customersRegion(GemFireCache gemfireCache) {
        ClientRegionFactoryBean<Long, com.mycompany.app.domain.Customer> customersRegion = new ClientRegionFactoryBean<>();
        customersRegion.setCache(gemfireCache);
        customersRegion.setClose(false);
        customersRegion.setShortcut(ClientRegionShortcut.PROXY);
        return customersRegion;
    }
}