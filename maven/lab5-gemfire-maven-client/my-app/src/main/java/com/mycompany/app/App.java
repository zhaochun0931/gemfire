package com.mycompany.app;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Set;

public class App {

    public static void main(String[] args) {
        String host = "172.16.204.139";
        int port = 1099;

        try {
            String urlString = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
            JMXServiceURL url = new JMXServiceURL(urlString);

            System.out.println("Connecting to JMX Manager at: " + urlString);
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            System.out.println("Connected successfully!\n");

            // UPDATED QUERY: Cast a wider net to find ANY region MBean on the locator
            ObjectName query = new ObjectName("GemFire:*,service=Region");

            Set<ObjectName> mbeanNames = mbsc.queryNames(query, null);

            if (mbeanNames.isEmpty()) {
                System.out.println("Still no regions found. The locator might not have initialized the Cluster Configuration Service yet.");
            } else {
                System.out.println("--- Hidden/System Regions Found ---");

                for (ObjectName name : mbeanNames) {
                    // Get the region name from the ObjectName
                    String regionName = name.getKeyProperty("name");

                    try {
                        // Fetch the live entry count
                        Object entryCount = mbsc.getAttribute(name, "EntryCount");
                        System.out.format("Region Name: %-25s | Entry Count: %s\n", regionName, entryCount);
                    } catch (Exception e) {
                        // If it fails to get EntryCount, just print the raw MBean path so we can see it
                        System.out.println("Found Region MBean (Raw Path): " + name.toString());
                    }
                }
            }

            jmxc.close();

        } catch (Exception e) {
            System.err.println("Failed to fetch JMX data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}