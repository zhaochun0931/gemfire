https://tanzu.vmware.com/developer/data/gemfire/blog/security-manager-basics-authentication-and-authorization/








https://gemfire.dev/blog/gemfire-security-manager-basics-authentication-only/




start locator --name=locator1 --J=-Dgemfire.security-manager=App --classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar

connect --locator=10.0.0.4[10334] --user=admin --password=password

start server --name=server1 --locators=localhost[10334] --classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar --user=admin --password=password

start server --name=server1 --locators=localhost[10334] --classpath=/root/my-app/target/my-app-1.0-SNAPSHOT.jar --user=admin --password=password --start-rest-api=yes --http-service-bind-address=localhost --http-service-port=8080


create region --name=helloWorld --type=PARTITION


curl -i --user admin:password http://localhost:8080/gemfire-api/v1







client:






