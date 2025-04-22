// Main.java

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.io.IOException;

public class Main {

    // 1) MBean interface
    public interface HelloMBean {
        void sayHello();
        int getCacheSize();
        void setCacheSize(int size);
    }

    // 2) MBean implementation
    public static class Hello implements HelloMBean {
        private int cacheSize = 100;

        @Override
        public void sayHello() {
            System.out.println("Hello, JMX!");
        }

        @Override
        public int getCacheSize() {
            return cacheSize;
        }

        @Override
        public void setCacheSize(int size) {
            this.cacheSize = size;
            System.out.println("Cache size set to " + size);
        }
    }

    // 3) Register and expose via JMX
    public static void main(String[] args) throws Exception {
        // Get the Platform MBeanServer
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Construct the ObjectName for our MBean
        ObjectName name = new ObjectName("com.example:type=Hello");

        // Create and register the MBean
        Hello mbean = new Hello();
        mbs.registerMBean(mbean, name);

        System.out.println("Hello MBean registered under name " + name);
        System.out.println("Press Enter to exit and unregister the MBean.");

        // Keep the JVM alive until user presses Enter
        System.in.read();

        // Cleanup (optional)
        mbs.unregisterMBean(name);
        System.out.println("MBean unregistered. Exiting.");
    }
}
