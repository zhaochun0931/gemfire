
start locator --name=locator1 --J=-Dgemfire.security-manager=App --classpath=/Users/zchun/Downloads/362449/0927/my-app/target/my-app-1.0-SNAPSHOT.jar:/Users/zchun/Downloads/362449/0927/my-app/target/class



gfsh>start locator --name=locator1 --J=-Dgemfire.security-manager=App --classpath=/Users/zchun/Downloads/362449/0927/my-app/target/my-app-1.0-SNAPSHOT.jar:/Users/zchun/Downloads/362449/0927/my-app/target/class
Starting a Geode Locator in /Users/zchun/Downloads/362449/0927/my-app/locator1...
...........
Locator in /Users/zchun/Downloads/362449/0927/my-app/locator1 on 192.168.1.4[10334] as locator1 is currently online.
Process ID: 9732
Uptime: 6 seconds
Geode Version: 9.15.1
Java Version: 11.0.18
Log File: /Users/zchun/Downloads/362449/0927/my-app/locator1/locator1.log
JVM Arguments: --add-exports=java.management/com.sun.jmx.remote.security=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -Dgemfire.enable-cluster-configuration=true -Dgemfire.load-cluster-configuration-from-dir=false -Dgemfire.security-manager=App -Dgemfire.launcher.registerSignalHandlers=true -Djava.awt.headless=true -Dsun.rmi.dgc.server.gcInterval=9223372036854775806
Class-Path: /Users/zchun/Downloads/vmware-gemfire-9.15.1/lib/geode-core-9.15.1.jar:/Users/zchun/Downloads/362449/0927/my-app/target/my-app-1.0-SNAPSHOT.jar:/Users/zchun/Downloads/362449/0927/my-app/target/class:/Users/zchun/Downloads/vmware-gemfire-9.15.1/lib/geode-server-all-9.15.1.jar

Unable to auto-connect (Security Manager may be enabled). Please use "connect --locator=192.168.1.4[10334] --user --password" to connect Gfsh to the locator.

Authentication required to connect to the Manager.

gfsh>connect
Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=192.168.1.4, port=1099] ..
user: myuser
password: **********
Successfully connected to: [host=192.168.1.4, port=1099]

You are connected to a cluster of version: 9.15.1

gfsh>
