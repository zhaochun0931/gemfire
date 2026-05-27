package com.example.myproject;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gemfire.outbound.CacheWritingMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class GemFireIntegrationConfig {

    // 1. Establish connection to the GemFire Standalone Server
    @Bean
    public GemFireCache gemfireCache() {
        return new ClientCacheFactory()
                .addPoolLocator("localhost", 10334) // Point to your running locator
                .create();
    }

    // 2. Define a proxy bean for the server-side 'DataRegion'
    @Bean(name = "DataRegion")
    public Region<String, String> dataRegion(GemFireCache cache) {
        ClientRegionFactory<String, String> regionFactory = 
            ((org.apache.geode.cache.client.ClientCache) cache)
            .createClientRegionFactory(ClientRegionShortcut.PROXY);
        return regionFactory.create("DataRegion");
    }

    // 3. Define the Spring Integration Channel
    @Bean
    public MessageChannel gemfireWriteChannel() {
        return new DirectChannel();
    }

    // 4. Define the Outbound Channel Adapter
    // It listens to 'gemfireWriteChannel' and writes message payloads to 'DataRegion'
    @Bean
    @ServiceActivator(inputChannel = "gemfireWriteChannel")
    public MessageHandler cacheWritingMessageHandler(Region<String, String> dataRegion) {
        CacheWritingMessageHandler handler = new CacheWritingMessageHandler(dataRegion);
        
        // Parse the SpEL strings into actual Spring Expression objects
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression keyExpression = parser.parseExpression("headers['itemKey']");
        Expression valueExpression = parser.parseExpression("payload");

        // Pass the Map<Expression, Expression> to the handler
        handler.setCacheEntryExpressions(java.util.Collections.singletonMap(keyExpression, valueExpression));
        
        return handler;
    }
}
