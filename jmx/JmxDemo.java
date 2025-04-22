import javax.management.*;
import javax.management.remote.*;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;

public class JmxDemo {

    // 1) MBean interface
    public interface HelloMBean {
        void sayHello();
        int getValue();
        void setValue(int val);
    }

    // 2) MBean implementation
    public static class Hello implements HelloMBean {
        private int value = 42;

        @Override
        public void sayHello() {
            System.out.println("Hello from JMX MBean!");
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public void setValue(int val) {
            this.value = val;
            System.out.println("Value changed to " + val);
        }
    }

    public static void main(String[] args) throws Exception {
        // Start an RMI registry on port 1099
        LocateRegistry.createRegistry(1099);
        System.out.println("RMI registry started on port 1099");

        // Get the Platform MBeanServer
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Register our Hello MBean
        ObjectName helloName = new ObjectName("com.demo:type=Hello");
        Hello helloMBean = new Hello();
        mbs.registerMBean(helloMBean, helloName);
        System.out.println("MBean registered under name: " + helloName);

        // Build the JMXConnectorServer
        JMXServiceURL url = new JMXServiceURL(
            "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"
        );
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(
            url, null, mbs
        );
        cs.start();
        System.out.println("JMXConnectorServer started at: " + url);

        // Keep the application running
        System.out.println("Application ready. Connect with JConsole to localhost:1099");
        Thread.sleep(Long.MAX_VALUE);
    }
}
