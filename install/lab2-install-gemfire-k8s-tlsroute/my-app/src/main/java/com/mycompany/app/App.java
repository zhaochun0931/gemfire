package com.mycompany.app;



import org.apache.geode.cache.client.proxy.ProxySocketFactories;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

// New imports for Region operations
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;




public class App {

    public static void main(String[] args) {
        
        // 1. Define the directory where your downloaded certificates are stored.
        // This corresponds to the "mkdir certs" step in the documentation.
        File certsDirectory = new File("./certs");
        
        // 2. Define the password for your keystore and truststore.
        // You should replace this string with the actual password decoded from the 'my-gemfire-cluster-cert' secret.
        String storepass = "YOUR_DECODED_PASSWORD";

        // 3. Set up the GemFire properties for SSL
        Properties props = new Properties();
        props.setProperty("ssl-enabled-components", "all");
        props.setProperty("ssl-endpoint-identification-enabled", "true");
        props.setProperty("ssl-keystore", Paths.get(certsDirectory.getAbsolutePath(), "keystore.p12").toString());
        props.setProperty("ssl-keystore-password", storepass);
        props.setProperty("ssl-truststore", Paths.get(certsDirectory.getAbsolutePath(), "truststore.p12").toString());
        props.setProperty("ssl-truststore-password", storepass);

        System.out.println("Attempting to connect to Tanzu GemFire cluster via SNI Gateway...");

        ClientCache cache = null;
        try {
            // 4. Create the ClientCache using the SNI ProxySocketFactory
            cache = new ClientCacheFactory(props)
                   // Replace "34.132.163.165" with the EXTERNAL-IP of your Gateway (from 'kubectl get gateways')
                   // Replace 9000 with your Gateway listener port
                   .setPoolSocketFactory(ProxySocketFactories.sni("localhost", 9000))
                   // Replace with your actual locator address and port
                   .addPoolLocator("gemfire1-locator-0.gemfire1-locator.default.svc.cluster.local", 10334)
                   .create();

            System.out.println("Successfully connected to the Tanzu GemFire cluster!");

            // ---------------------------------------------------------
            // TODO: Perform your Region creation and Cache operations here
            // Example: Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY).create("myRegion");
            // ---------------------------------------------------------


// =========================================================
            // NEW CODE: Region Creation and Data Insertion
            // =========================================================
            
            // 5. Create a client proxy region linking to the server's "exampleRegion"
            System.out.println("Creating client proxy for 'exampleRegion'...");
            Region<String, String> region = cache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                    .create("exampleRegion");

            // 6. Insert entries into the region (Put)
            System.out.println("Inserting entries into exampleRegion...");
            region.put("user101", "Alice");
            region.put("user102", "Bob");
            region.put("user103", "Charlie");
            System.out.println("Successfully inserted 3 entries.");

            // 7. Retrieve and print an entry to verify (Get)
            String fetchedValue = region.get("user102");
            System.out.println("Retrieving 'user102' from exampleRegion: " + fetchedValue);
            
            // =========================================================


        } catch (Exception e) {
            System.err.println("Failed to connect to the GemFire cluster.");
            e.printStackTrace();
        } finally {
            // 5. Cleanly close the cache connection upon exit
            if (cache != null && !cache.isClosed()) {
                cache.close();
                System.out.println("Connection closed.");
            }
        }
    }
}
